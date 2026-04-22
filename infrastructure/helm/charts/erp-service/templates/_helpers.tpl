{{- define "erp-service.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "erp-service.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- printf "%s" $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{- define "erp-service.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" -}}
{{- end -}}

{{- define "erp-service.selectorLabels" -}}
app.kubernetes.io/name: {{ include "erp-service.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}

{{- define "erp-service.labels" -}}
helm.sh/chart: {{ include "erp-service.chart" . }}
{{ include "erp-service.selectorLabels" . }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end -}}

{{- define "erp-service.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
{{- default (include "erp-service.fullname" .) .Values.serviceAccount.name -}}
{{- else -}}
{{- default "default" .Values.serviceAccount.name -}}
{{- end -}}
{{- end -}}

{{- define "erp-service.externalSecretName" -}}
{{- default (printf "%s-runtime" (include "erp-service.fullname" .)) .Values.externalSecret.targetName -}}
{{- end -}}
