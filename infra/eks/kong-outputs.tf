# Kong outputs removed: Kong is now deployed via kubectl in CI (deploy-app)
# Outputs referencing kubernetes_service.* caused terraform plan to fail because the
# kubernetes provider resources are not managed by Terraform anymore.
# If endpoints are required for downstream steps, capture them in the CI pipeline via kubectl:
# kubectl get svc kong-proxy -n officyna -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'
# kubectl get svc kong-admin -n officyna -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'
