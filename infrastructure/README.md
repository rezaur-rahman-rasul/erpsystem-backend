# Infrastructure Layout

This folder is the production deployment starting point for the ERP platform.

## Structure

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

## Usage

1. Provision AWS infrastructure separately:
   - VPC
   - EKS
   - RDS
   - ElastiCache
   - MSK
   - ECR
2. Install cluster add-ons:
   - AWS Load Balancer Controller
   - External Secrets Operator
   - Metrics Server
   - Karpenter
   - ADOT / CloudWatch / Fluent Bit stack
3. Update placeholders in this repo:
   - ECR image repositories
   - ACM certificate ARN
   - WAF ACL ARN
   - domain names
   - repo URL in Argo CD Application manifests
   - AWS region in the ClusterSecretStore
4. Create the expected Secrets Manager secrets.
5. Sync `gitops/bootstrap`.
6. Sync either `gitops/nonprod/applications.yaml` or `gitops/prod/applications.yaml`.

## Important Gaps

- The application code still uses a shared JWT secret model. Move to JWKS before a real production launch.
- Flyway still runs inside the app startup path. Split that into migration jobs before high-availability cutover.
- Secret values are placeholders only. Nothing in this folder should be applied without replacing them.
