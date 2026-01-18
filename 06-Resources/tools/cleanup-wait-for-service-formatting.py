#!/usr/bin/env python3
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

"""
Clean up extra blank lines in wait_for_service() calls
"""

import os
import re

# Color codes
GREEN = '\033[0;32m'
YELLOW = '\033[1;33m'
BLUE = '\033[0;34m'
NC = '\033[0m'

def print_header(msg):
    print(f"\n{BLUE}=== {msg} ==={NC}\n")

def print_success(msg):
    print(f"{GREEN}✓{NC} {msg}")

def print_info(msg):
    print(f"{YELLOW}ℹ{NC} {msg}")

def cleanup_formatting(file_path):
    """Remove extra blank lines in wait_for_service() call"""
    
    with open(file_path, 'r') as f:
        content = f.read()
    
    # Pattern to match wait_for_service with extra blank lines
    pattern = r'([ \t]+)# Wait for database to be ready\n' \
              r'(?:\n[ \t]*\n)*' \
              r'[ \t]+wait_for_service "Database" \\\n' \
              r'(?:\n[ \t]*\n)*' \
              r'[ \t]+"podman exec[^\n]+\n' \
              r'(?:\n[ \t]*\n)*' \
              r'[ \t]+"\$DB_READY_TIMEOUT" \\\n' \
              r'(?:\n[ \t]*\n)*' \
              r'[ \t]+"\$HEALTH_CHECK_INTERVAL"'
    
    # Clean replacement
    replacement = r'\1# Wait for database to be ready\n' \
                  r'\1wait_for_service "Database" \\\n' \
                  r'\1    "podman exec \"$DB_CONTAINER\" pg_isready -U \"$DB_USER\" -d \"$DB_NAME\"" \\\n' \
                  r'\1    "$DB_READY_TIMEOUT" \\\n' \
                  r'\1    "$HEALTH_CHECK_INTERVAL"'
    
    new_content = re.sub(pattern, replacement, content)
    
    if new_content != content:
        with open(file_path, 'w') as f:
            f.write(new_content)
        return True
    
    return False

def main():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.abspath(os.path.join(script_dir, '../../..'))
    
    labs = [
        "Lab03-JPA",
        "Lab04-CDI",
        "Lab04B-EJB",
        "Lab05-REST",
        "Lab06-DDD",
        "Lab07-Hexagonal",
        "Lab09-Security"
    ]
    
    print_header("Cleaning up wait_for_service() Formatting")
    
    success_count = 0
    
    for lab in labs:
        lab_dir = os.path.join(project_root, 'esipe-javaee/03-Labs', lab)
        script_file = os.path.join(lab_dir, 'podman-test.sh')
        
        if not os.path.exists(script_file):
            continue
        
        print_info(f"Processing {lab}...")
        
        if cleanup_formatting(script_file):
            print_success(f"  Cleaned up formatting")
            success_count += 1
        else:
            print_info(f"  No changes needed")
    
    print_header("Summary")
    print_success(f"Cleaned up {success_count}/{len(labs)} labs")

if __name__ == '__main__':
    main()

# Made with Bob
