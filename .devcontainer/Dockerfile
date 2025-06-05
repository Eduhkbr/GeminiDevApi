# .devcontainer/Dockerfile
ARG VARIANT="21"
FROM mcr.microsoft.com/devcontainers/java:${VARIANT}

# instalar Maven via apt
RUN apt-get update \
 && apt-get install -y maven \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*