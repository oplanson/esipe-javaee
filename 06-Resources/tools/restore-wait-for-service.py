#!/usr/bin/env python3
# © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.

"""
Restore wait_for_service() function usage in Phase 3
Now that container names are fixed, the function should work
"""

import os
import sys
import re

# Color codes
RED = '\033[0;31m'
GREEN = '\033[0;32m'
YELLOW = '\033[1;33m'
BLUE = '\033[0;34m'
NC = '\033[0m'

def print_header(msg):
    print(f"\n{BLUE}=== {msg} ==={NC}\n")

def print_success(msg):
    print(f"{GREEN}✓{NC} {msg}")

def print_error(msg):
    print(f"{RED}✗{NC} {msg}")

def print_info(msg):
    print(f"{YELLOW}ℹ{NC} {msg}")

def restore_wait_for_service(file_path):
    """Replace inline database wait with wait_for_service() call"""
    
    with open(file_path, 'r') as f:
        content = f.read()
    
    # Pattern to match the inline database wait code (including extra blank lines from previous replacement)
    pattern = r'(\s+)# Wait for database to be ready\n' \
              r'(?:\n\s+\n)*' \
              r'\s+wait_for_service "Database" \\\n' \
              r'(?:\n\s+\n)*' \
              r'\s+"podman exec \\"[^"]+\\" pg_isready[^"]+"\s+\\\n' \
              r'(?:\n\s+\n)*' \
              r'\s+"\$DB_READY_TIMEOUT"\s+\\\n' \
              r'(?:\n\s+\n)*' \
              r'\s+"\$HEALTH_CHECK_INTERVAL"'
    
    # Also match the original inline pattern
    pattern_inline = r'(\s+)# Wait for database to be ready\n' \
                     r'\s+print_info "Waiting for Database to be ready\.\.\."\n' \
                     r'\s+local elapsed=0\n' \
                     r'\s+local db_ready=false\n' \
                     r'\s+while \[ "\$elapsed" -lt "\$DB_READY_TIMEOUT" \]; do\n' \
                     r'(?:\s+print_info "echo DB";\n)?' \
                     r'\s+if podman exec "\$DB_CONTAINER" pg_isready -U "\$DB_USER" -d "\$DB_NAME"[^;]*; then\n' \
                     r'\s+print_success "Database is ready! \(\$\{elapsed\}s\)"\n' \
                     r'\s+db_ready=true\n' \
                     r'\s+break\n' \
                     r'\s+fi\n' \
                     r'\s+echo -n "\."\n' \
                     r'\s+sleep "\$HEALTH_CHECK_INTERVAL"\n' \
                     r'\s+elapsed=\$\(\(elapsed \+ HEALTH_CHECK_INTERVAL\)\)\n' \
                     r'\s+done\n' \
                     r'\s+\n' \
                     r'\s+if \[ "\$db_ready" = "false" \]; then\n' \
                     r'\s+echo ""\n' \
                     r'\s+print_error "Database failed to start within \$\{DB_READY_TIMEOUT\}s"\n' \
                     r'\s+exit 1\n' \
                     r'\s+fi'
    
    # Replacement with wait_for_service() call
    replacement = r'\1# Wait for database to be ready\n' \
                  r'\1wait_for_service "Database" \\\n' \
                  r'\1    "podman exec \\"$DB_CONTAINER\\" pg_isready -U \\"$DB_USER\\" -d \\"$DB_NAME\\"" \\\n' \
                  r'\1    "$DB_READY_TIMEOUT" \\\n' \
                  r'\1    "$HEALTH_CHECK_INTERVAL"'
    
    # Perform replacement - try both patterns
    new_content = re.sub(pattern, replacement, content)
    if new_content == content:
        new_content = re.sub(pattern_inline, replacement, content)
    
    if new_content != content:
        # Create backup
        backup_path = file_path + '.bak'
        with open(backup_path, 'w') as f:
            f.write(content)
        
        # Write new content
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
    
    print_header("Restoring wait_for_service() Function Usage")
    
    success_count = 0
    
    for lab in labs:
        lab_dir = os.path.join(project_root, 'esipe-javaee/03-Labs', lab)
        script_file = os.path.join(lab_dir, 'podman-test.sh')
        
        if not os.path.exists(script_file):
            print_error(f"{lab}: podman-test.sh not found")
            continue
        
        print_info(f"Processing {lab}...")
        
        if restore_wait_for_service(script_file):
            print_success(f"  Restored wait_for_service() call")
            print_success(f"{lab}: Fixed")
            success_count += 1
        else:
            print_info(f"{lab}: No changes needed (already using function or pattern not found)")
    
    print_header("Summary")
    print_success(f"Restored wait_for_service() in {success_count}/{len(labs)} labs")
    print()
    print_info("The function now works correctly because:")
    print("  1. Container names match docker-compose.yml")
    print("  2. Command uses podman exec (not docker exec)")
    print("  3. Variables will expand properly in the function context")

if __name__ == '__main__':
    main()

# Made with Bob
