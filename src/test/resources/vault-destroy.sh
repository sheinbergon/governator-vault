#!/usr/bin/env bash

# Running the vault server
echo "Killing processes containing 'vault-config' in their executable's name"
pkill -9 -f "vault-config"
