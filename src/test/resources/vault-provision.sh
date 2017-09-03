#!/usr/bin/env bash

# TODO - Add kill signal handling

# Binaries
WGET=`which wget`
UNZIP=`which unzip`

# Definitions
VERSION="0.8.1"
DOWNLOAD_TARGET="vault.zip"
DOWNLOAD_URL="https://releases.hashicorp.com/vault/${VERSION}/vault_${VERSION}_linux_amd64.zip"
ROOT_TOKEN=1913f7f4-ff4b-4ac2-baf0-5e14655ad8b3

# Download Vault
echo "Downloading vault from ${DOWNLOAD_URL}"
${WGET} -q -O ${DOWNLOAD_TARGET} ${DOWNLOAD_URL}

# Unzip the archive
echo "Unzipping the archive"
${UNZIP} -q ${DOWNLOAD_TARGET}

# TODO - Add timeout configuration
# Running the vault server
echo "Running the server in dev mode - './vault server -dev -dev-root-token-id=${ROOT_TOKEN} -config=vault-config.hcl >/dev/null 2>&1 0>&- &'"
./vault server -dev -dev-root-token-id=${ROOT_TOKEN} -config=vault-config.hcl >/dev/null 2>&1 0>&- &