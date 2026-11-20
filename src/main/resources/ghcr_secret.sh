microk8s kubectl create secret docker-registry ghcr-secret \
--docker-server=ghcr.io \
--docker-username=ameyas2 \
--docker-password='YOUR_GITHUB_PAT' \
--docker-email='YOUR_EMAIL' \
--namespace=default