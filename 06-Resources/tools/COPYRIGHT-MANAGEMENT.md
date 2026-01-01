# Copyright Management Tools

This directory contains three scripts for managing copyright notices across the project.

## Scripts Overview

### 1. `update-copyrights.sh` - Bulk Update
**Purpose:** Update ALL existing copyright notices to the new format, regardless of file modification status.

**Use Case:** Initial migration or format change

**What it does:**
- Finds all files with existing copyright notices
- Updates them to the new format: `© Copyright 2025-CURRENT_YEAR Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.`
- Changes single year (2025) to year range (2025-CURRENT_YEAR)
- Uses current system year automatically
- Processes: Markdown, Shell, XML, HTML, JSP, Java, Python files

**Usage:**
```bash
cd esipe-javaee
bash 06-Resources/tools/update-copyrights.sh
```

**Example Output:**
```
✓ Updated: ./03-Labs/Lab01-FirstServlet/README.md
✓ Updated: ./03-Labs/Lab01-FirstServlet/podman-test.sh
⏭️  Skipped (no copyright): ./03-Labs/Lab02-ServletsJSP/solution/src/main/java/Client.java
```

---

### 2. `add-missing-copyrights.sh` - Add to New Files
**Purpose:** Add copyright notices to files that don't have one yet.

**Use Case:** Adding copyrights to newly created files or files without copyright

**What it does:**
- Scans HTML, JSP, and Java files
- Adds copyright notice with current year at the beginning of files without one
- Skips files that already have a copyright
- Preserves file structure (adds before package declaration in Java)
- Uses current system year automatically

**Usage:**
```bash
cd esipe-javaee
bash 06-Resources/tools/add-missing-copyrights.sh
```

**Example Output:**
```
✓ Added copyright: ./03-Labs/Lab04-CDI/solution/src/main/webapp/index.html
✓ Added copyright: ./03-Labs/Lab05-REST/solution/src/main/java/com/bank/api/AccountResource.java
⏭️  Already has copyright: ./03-Labs/Lab01-FirstServlet/solution/src/main/java/Client.java
```

---

### 3. `smart-update-copyrights.sh` - Intelligent Update (RECOMMENDED)
**Purpose:** Intelligently update copyright years based on Git modification history.

**Use Case:** Annual copyright updates or after making changes to files

**What it does:**
- Uses Git to detect when each file was last modified
- Only updates copyright if file was modified in the current year
- Preserves original creation year
- Creates or updates year range (e.g., 2025 → 2025-2026)
- Skips files not modified this year
- Skips files already showing current year

**Logic:**
```
File created in 2025, modified in CURRENT_YEAR:
  Before: © Copyright 2025 Olivier Planson...
  After:  © Copyright 2025-CURRENT_YEAR Olivier Planson...

File created in 2025, not modified in CURRENT_YEAR:
  Before: © Copyright 2025 Olivier Planson...
  After:  © Copyright 2025 Olivier Planson... (no change)

File already has CURRENT_YEAR:
  Before: © Copyright 2025-CURRENT_YEAR Olivier Planson...
  After:  © Copyright 2025-CURRENT_YEAR Olivier Planson... (no change)
```

**Note:** CURRENT_YEAR is automatically determined from the system date.

**Usage:**
```bash
cd esipe-javaee
bash 06-Resources/tools/smart-update-copyrights.sh
```

**Example Output:**
```
Current year: 2026
✓ Updated: ./03-Labs/Lab05-REST/solution/src/main/java/com/bank/api/AccountResource.java (2025 → 2025-2026)
⏭️  Not modified this year: ./03-Labs/Lab01-FirstServlet/README.md (last: 2025, copyright: 2025)
⏭️  Already current: ./03-Labs/Lab04-CDI/solution/src/main/webapp/index.html (2025-2026)
```

**Note:** The year 2026 in this example would be the current system year when the script runs.

---

## Copyright Format

All scripts use the same copyright format:

**HTML/XML:**
```html
<!-- © Copyright 2025-CURRENT_YEAR Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
```

**Java:**
```java
/* © Copyright 2025-CURRENT_YEAR Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */
```

**JSP:**
```jsp
<%-- © Copyright 2025-CURRENT_YEAR Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>
```

**Shell/Python/Markdown:**
```bash
# © Copyright 2025-CURRENT_YEAR Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
```

**Note:** CURRENT_YEAR is automatically replaced with the actual current year (e.g., 2026, 2027, etc.)

---

## Workflow Recommendations

### Initial Setup (First Time)
1. Run `add-missing-copyrights.sh` to add copyrights to all files
2. Run `update-copyrights.sh` to ensure consistent format
3. Commit changes

### Annual Update (Every Year)
1. Run `smart-update-copyrights.sh` at the beginning of the year
2. Review changes
3. Commit with message: `docs: update copyright year to YYYY`

### New Files
1. Manually add copyright when creating new files, OR
2. Run `add-missing-copyrights.sh` periodically

### Format Change
1. Update the copyright format in all three scripts
2. Run `update-copyrights.sh` to apply new format to all files
3. Commit changes

---

## Git Integration

The `smart-update-copyrights.sh` script requires Git to function properly. It uses:
- `git log` to determine when files were last modified
- `git rev-parse` to verify it's in a Git repository

**Requirements:**
- Must be run from within a Git repository
- Files must be tracked by Git
- Git history must be available

---

## Automation

### Pre-commit Hook Integration

You can integrate copyright updates into the pre-commit hook:

```bash
# Add to .git/hooks/pre-commit
echo "Updating copyrights for modified files..."
bash 06-Resources/tools/smart-update-copyrights.sh
```

### CI/CD Integration

Add to your CI/CD pipeline to verify copyrights:

```yaml
# Example GitHub Actions
- name: Check Copyright Years
  run: |
    bash 06-Resources/tools/smart-update-copyrights.sh
    if [ -n "$(git status --porcelain)" ]; then
      echo "Copyright years need updating"
      exit 1
    fi
```

---

## Troubleshooting

### Script Not Executable
```bash
chmod +x 06-Resources/tools/*.sh
```

### sed: command not found (macOS)
The scripts use `sed -i ''` for macOS compatibility. For Linux, change to `sed -i`.

### Git History Not Found
If `smart-update-copyrights.sh` shows "No Git history", ensure:
- File is tracked by Git: `git add <file>`
- Repository has commits: `git log`

### Copyright Not Detected
Ensure copyright format matches exactly:
```
© Copyright YYYY[-YYYY] Olivier Planson
```

---

## File Support

| File Type | Extension | Supported |
|-----------|-----------|-----------|
| Java      | .java     | ✅        |
| Markdown  | .md       | ✅        |
| Shell     | .sh       | ✅        |
| XML       | .xml      | ✅        |
| HTML      | .html     | ✅        |
| JSP       | .jsp      | ✅        |
| Python    | .py       | ✅        |
| CSS       | .css      | ❌        |
| JavaScript| .js       | ❌        |
| Properties| .properties| ❌       |

To add support for new file types, update the scripts' file patterns and sed commands.

---

## Best Practices

1. **Always review changes** before committing
2. **Use smart-update-copyrights.sh** for annual updates
3. **Add copyrights to new files** immediately
4. **Keep format consistent** across all files
5. **Document any format changes** in commit messages
6. **Test scripts** on a small set of files first

---

## Examples

### Scenario 1: New Year Update
```bash
# January 1st, 2027
cd esipe-javaee
bash 06-Resources/tools/smart-update-copyrights.sh
# Only files modified in 2027 will be updated to 2025-2027
git add .
git commit -m "docs: update copyright year to 2027"
```

### Scenario 2: New Lab Created
```bash
# After creating Lab06
cd esipe-javaee
bash 06-Resources/tools/add-missing-copyrights.sh
# Adds copyright to all new files in Lab06
git add 03-Labs/Lab06
git commit -m "feat: add Lab06 with copyright notices"
```

### Scenario 3: Format Change
```bash
# Change copyright format in all three scripts
cd esipe-javaee
bash 06-Resources/tools/update-copyrights.sh
# Updates all existing copyrights to new format
git add .
git commit -m "docs: update copyright format"
```

---

## Support

For issues or questions about copyright management:
1. Check this documentation
2. Review script comments
3. Test on a single file first
4. Contact the maintainer

---

**Last Updated:** January 2026  
**Maintainer:** Olivier Planson  
**License:** All rights reserved. Reproduction prohibited. Made with IBM Bob.