# Production Deployment Blueprint

## Scope

This document turns the Phase 1 ERP services into a concrete AWS production target with:

- account layout
- VPC and EKS topology
- namespace model
- platform add-ons
- GitOps structure
- reusable Helm chart structure
- concrete environment values for:
  - `gateway`
  - `identity-access`
  - `organization`
  - `master-data`

The supporting artifacts live under `infrastructure/`.

## Target AWS Landing Zone

Use separate AWS accounts. Do not run production and nonproduction in the same account.

- `shared-services`
  - ECR
  - CI runners
  - central artifact signing
  - central AMP / AMG if you want shared observability
- `nonprod`
  - one EKS cluster
  - nonprod RDS / Redis / MSK
  - developer and QA workloads
- `prod`
  - one EKS cluster
  - production RDS / Redis / MSK
  - internet-facing ALB and WAF
- `security-log-archive`
  - CloudTrail aggregation
  - AWS Config
  - GuardDuty / Security Hub aggregation
  - backup vault copies and immutable retention if required

## Network Topology

Each application account uses one VPC across three Availability Zones.

- public subnets
  - internet-facing ALB only
  - no worker nodes
- private application subnets
  - EKS worker nodes
  - VPC endpoints where practical
- private data subnets
  - RDS
  - ElastiCache
  - MSK

Use interface or gateway endpoints for:

- ECR API
- ECR DKR
- S3
- CloudWatch Logs
- STS
- Secrets Manager
- X-Ray if used

## EKS Topology

Use one cluster per environment.

- `erp-nonprod-eks`
- `erp-prod-eks`

Control plane:

- private endpoint enabled
- public endpoint restricted to known admin CIDRs or VPN

Worker model:

- EKS managed node group for baseline system workloads
- Karpenter for application workloads after cluster bootstrap is stable
- on-demand nodes for production synchronous APIs
- spot nodes only for noncritical batch or async consumers

Recommended namespaces per cluster:

- `erp-gateway`
- `erp-identity-access`
- `erp-organization`
- `erp-master-data`
- `argocd`
- `external-secrets`
- `aws-observability`

## Managed Service Choices

### PostgreSQL

Use `Amazon RDS for PostgreSQL` with `RDS Proxy`.

- production: Multi-AZ DB cluster
- nonprod: Multi-AZ DB instance or cluster depending budget
- keep database-per-service isolation using separate databases and users
- start with one PostgreSQL cluster hosting multiple service databases if cost matters
- isolate `identity-access` first if you later split clusters

### Redis

Use `ElastiCache for Redis/Valkey` with:

- Multi-AZ
- automatic failover
- transit encryption
- auth token

### Kafka

Use `Amazon MSK Provisioned` across three AZs with:

- replication factor `3`
- minimum in-sync replicas `2`
- SASL/SCRAM over `9096` from inside AWS
- broker and client TLS enabled

Avoid self-hosting Kafka or Redis inside Kubernetes.

## Public Entry and TLS

Frontend:

- CloudFront in front of S3
- one distribution for the Angular shell and remote assets

API:

- `gateway` stays the only public backend workload
- expose `gateway` through `AWS Load Balancer Controller`
- use an internet-facing ALB
- terminate TLS at the ALB with ACM certificates
- attach AWS WAF to the ALB

`cert-manager` decision:

- not required for public ALB certificates on AWS
- install only if you need internal certificates, webhook cert rotation, or private PKI flows

## Platform Add-Ons

Required add-ons:

- `AWS Load Balancer Controller`
- `External Secrets Operator`
- `EKS Pod Identity Agent`
- `Metrics Server`
- `Karpenter`
- `ADOT Collector`
- `Fluent Bit`

Recommended observability destinations:

- metrics: `Amazon Managed Service for Prometheus`
- dashboards: `Amazon Managed Grafana`
- traces: `AWS X-Ray`
- logs: `CloudWatch Logs`

Optional:

- `Loki` backed by S3 if you want Grafana-native log exploration
- `Argo Rollouts` for progressive delivery on internet-facing services

## Security Model

Kubernetes:

- `Pod Security` at restricted baseline
- per-service namespace isolation
- per-service ServiceAccounts
- ingress-restricting NetworkPolicies for internal services
- use security groups and VPC segmentation for managed-service egress controls

AWS:

- least-privilege IAM
- KMS customer-managed keys for RDS, MSK, Secrets Manager, S3, and backups
- no public database, cache, or broker endpoints
- WAF managed rules on the ALB
- CloudTrail, GuardDuty, Security Hub enabled org-wide

Application contracts that should be fixed before real production cutover:

- replace the shared symmetric JWT secret with asymmetric signing and JWKS publishing
- remove hard-coded bootstrap admin defaults from `application.yml`
- move Flyway execution out of the main Deployment path and into controlled migration jobs
- validate service behavior against Redis TLS and MSK SASL/SCRAM

## CI/CD Model

CI:

- GitHub Actions or equivalent
- build one image per service
- run unit and integration tests
- generate SBOM
- scan dependencies and images
- sign images
- publish immutable tags to ECR

CD:

- Argo CD watches this repo
- Helm chart is shared
- environment-specific values files drive nonprod and prod
- nonprod syncs automatically
- prod is PR-promoted and synced manually or via gated pipeline

Rollouts:

- `gateway` should use progressive delivery first
- internal services can stay on rolling update until failure blast radius justifies canary

## Helm and GitOps Layout

```text
infrastructure/
  gitops/
    bootstrap/
    nonprod/
    prod/
  helm/
    charts/
      erp-service/
    values/
      nonprod/
      prod/
```

Intent:

- `gitops/bootstrap`: namespaces and cluster-scoped bootstrap resources
- `gitops/nonprod` and `gitops/prod`: Argo CD Application objects
- `helm/charts/erp-service`: reusable chart for Spring Boot services
- `helm/values/*`: service-specific environment values

## Readiness and Liveness

Use Kubernetes probes aligned to Spring Boot Actuator.

- startup: `/actuator/health/liveness`
- liveness: `/actuator/health/liveness`
- readiness: `/actuator/health/readiness`

Rules:

- liveness must not depend on external systems
- readiness can depend on external systems only if the service cannot safely accept new traffic without them
- add graceful shutdown and ALB deregistration delay handling on `gateway`

## Secrets Strategy

Store secrets in AWS Secrets Manager, then sync them with External Secrets.

Suggested secret naming:

- `/erp/nonprod/gateway/runtime`
- `/erp/nonprod/identity-access/runtime`
- `/erp/nonprod/organization/runtime`
- `/erp/nonprod/master-data/runtime`
- `/erp/prod/gateway/runtime`
- `/erp/prod/identity-access/runtime`
- `/erp/prod/organization/runtime`
- `/erp/prod/master-data/runtime`

Keep nonsecret configuration in Helm values:

- hostnames
- service URLs
- resource requests and limits
- replica targets
- ingress hosts

Keep secrets in Secrets Manager:

- database users and passwords
- Redis auth token
- JWT signing keys or secrets
- Kafka SASL credentials
- bootstrap admin credentials until bootstrap is redesigned

## Disaster Recovery Baseline

Initial target:

- single-region HA first
- cross-region DR second

Recommended baseline:

- RDS PITR and AWS Backup cross-region copies
- Secrets Manager cross-region replication
- S3 versioning and replication for frontend assets and optional Loki objects
- MSK Replicator to a warm standby cluster in the DR region
- cluster recreation from IaC plus Argo CD, not hand-built recovery

## What The Infrastructure Folder Gives You

The `infrastructure/` folder added in this tranche gives you:

- bootstrap namespace manifests
- a reusable Helm chart for Spring Boot services
- per-service values for `nonprod` and `prod`
- Argo CD Application manifests for both environments

It does not yet give you:

- Terraform or OpenTofu for VPC / EKS / RDS / Redis / MSK provisioning
- controller installation manifests for Argo CD, External Secrets, Karpenter, or ADOT
- migration Jobs separate from app startup
- secret values or actual AWS ARNs

Those still need to be implemented in the infrastructure provisioning layer.
