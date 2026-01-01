# Lab Verification Script

## Overview

The `verify-all-labs.sh` script automates the testing of all Jakarta EE labs by executing each lab's `podman-test.sh` script and providing a comprehensive summary of results.

## Features

- ✅ **Automatically discovers and tests all labs** (no hardcoded lab names)
- 📊 Provides detailed test results with timing information
- 🎨 Color-coded output for easy reading
- 📈 Summary statistics (passed/failed/skipped)
- ⏱️ Duration tracking for each lab
- 🔍 Error details for failed tests

## Usage

### Basic Usage

```bash
# From the tools directory
cd esipe-javaee/06-Resources/tools
chmod +x verify-all-labs.sh
./verify-all-labs.sh
```

### From Project Root

```bash
# From the project root
bash esipe-javaee/06-Resources/tools/verify-all-labs.sh
```

## Output Format

The script provides three types of output:

### 1. Individual Lab Results

Each lab test shows:
- Lab name
- Test execution status (PASSED/FAILED/SKIPPED)
- Duration in seconds
- Number of tests passed (if available)
- Error details (if failed)

Example:
```
───────────────────────────────────────────────────────────────
  Testing: Lab05-REST
───────────────────────────────────────────────────────────────
Running tests...
✅ Lab05-REST: PASSED (45s)
   All 11 tests passed
```

### 2. Summary Statistics

```
═══════════════════════════════════════════════════════════════
  Test Results Summary
═══════════════════════════════════════════════════════════════

Total Labs Tested: 5
Passed: 5
Failed: 0
```

### 3. Detailed Results Table

```
Detailed Results:
┌─────────────────────────┬──────────┬──────────┐
│ Lab                     │ Status   │ Duration │
├─────────────────────────┼──────────┼──────────┤
│ Lab01-FirstServlet      │ ✅ PASSED │     12s  │
│ Lab02-ServletsJSP       │ ✅ PASSED │     18s  │
│ Lab03-JPA               │ ✅ PASSED │     25s  │
│ Lab04-CDI               │ ✅ PASSED │     32s  │
│ Lab05-REST              │ ✅ PASSED │     45s  │
└─────────────────────────┴──────────┴──────────┘

Total Duration: 132s
```

## Exit Codes

- `0`: All tests passed successfully
- `1`: One or more tests failed

## Prerequisites

- Podman or Docker installed and running
- All lab solutions must be built (`mvn clean package`)
- Sufficient system resources for running containers

## Labs Tested

The script **automatically discovers all labs** in the `03-Labs` directory. It will:

1. Search for all directories starting with "Lab" (e.g., `Lab01-*`, `Lab02-*`, etc.)
2. Sort them naturally (Lab01, Lab02, ..., Lab10, Lab11, etc.)
3. Test each lab that has a `podman-test.sh` script
4. Skip labs without test scripts

**Current labs** (as of 2026-01-01):
- Lab01-FirstServlet: Basic servlet implementation
- Lab02-ServletsJSP: Servlets and JSP pages
- Lab03-JPA: JPA and database integration
- Lab04-CDI: CDI and dependency injection
- Lab05-REST: JAX-RS REST services

**Future labs** will be automatically detected and tested without modifying the script.

## Adding New Labs

To add a new lab that will be automatically tested:

1. Create a new lab directory in `03-Labs/` with a name starting with "Lab" (e.g., `Lab06-Security`)
2. Add a `podman-test.sh` script in the lab directory
3. Make the script executable: `chmod +x podman-test.sh`
4. Run `verify-all-labs.sh` - your new lab will be automatically discovered and tested

**No script modification required!** The verification script will automatically find and test any new labs.

## Troubleshooting

### Script Not Executable

```bash
chmod +x esipe-javaee/06-Resources/tools/verify-all-labs.sh
```

### Lab Skipped

If a lab shows as "SKIPPED", it means the `podman-test.sh` file was not found in that lab's directory.

### Test Failures

When a test fails, the script displays the last 20 lines of error output. To see full details:

```bash
# Run the specific lab test manually
cd esipe-javaee/03-Labs/Lab05-REST
./podman-test.sh
```

### Container Issues

If containers fail to start:

```bash
# Clean up existing containers
podman ps -a | grep banking | awk '{print $1}' | xargs podman rm -f

# Clean up networks
podman network prune -f
```

## Integration with CI/CD

This script can be integrated into CI/CD pipelines:

```yaml
# Example GitHub Actions workflow
- name: Verify All Labs
  run: |
    chmod +x esipe-javaee/06-Resources/tools/verify-all-labs.sh
    bash esipe-javaee/06-Resources/tools/verify-all-labs.sh
```

## Related Scripts

- `podman-test.sh`: Individual lab test script (in each lab directory)
- `run-lab.sh`: Run a specific lab interactively
- `test-lab.sh`: Alternative test script for some labs

## Author

Olivier Planson - 2026

## License

© Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited.