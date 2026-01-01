# Copyright Management Guide

This guide explains how copyright notices are managed in the Jakarta EE course repository using the unified management script.

## Copyright Format

All files in this repository should include the following copyright notice:

```
© Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
```

## File-Specific Formats

### Category 1: Java Files (Internal Code)
```java
/* © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

package com.bank.model;

public class MyClass {
    // ...
}
```
**Placement:** After package declaration to avoid compilation errors

### Category 2: Source Code Files (Internal, Not User-Visible)

#### Shell Scripts
```bash
#!/bin/bash
# © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
```

#### Python Scripts
```python
#!/usr/bin/env python3
# © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob.
```

#### XML Files
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
```

#### Markdown Files
```markdown
<!-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->

# Document Title
```

### Category 3: User-Visible Files (Rendered in Browser)

#### HTML Files
```html
<!-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
<!DOCTYPE html>
<html>
    <!-- Content visible to users -->
</html>
```
**Note:** Copyright in HTML comment (internal), not visible in rendered page

#### JSP Files
```jsp
<%-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <!-- Content visible to users -->
</html>
```
**Note:** Copyright in JSP comment (internal), not visible in rendered page

---

## Unified Management Script

### Single Script for All Copyright Management
**File:** `manage-copyrights.sh`

This unified script handles all copyright management tasks:
- Updates copyright years for modified files (Git-based detection)
- Adds copyright to files without one
- Handles all file types correctly
- Respects file categories (internal vs user-visible)

**Features:**
- ✅ Smart detection: Only updates files modified in current year
- ✅ Preserves original creation year
- ✅ Creates or updates year ranges (e.g., 2025 → 2025-2026)
- ✅ Skips files already showing current year
- ✅ Adds copyright to new files automatically
- ✅ Correct placement for each file type
- ✅ Uses Git history to determine modifications

**Usage:**
```bash
cd 06-Resources/tools
./manage-copyrights.sh
```

**When to use:**
- Automatically runs in pre-commit hook
- Manual run for audits or verification
- After creating new files

---

## File Categories and Copyright Visibility

### 1. Internal Code (Java)
- **Visibility:** Not visible to end users
- **Format:** `/* ... */` comment after package declaration
- **Purpose:** Legal protection of source code

### 2. Source Files (sh, py, xml, md)
- **Visibility:** Not visible to end users
- **Format:** `#` or `<!-- ... -->` comments
- **Purpose:** Legal protection of configuration and documentation

### 3. User-Visible Files (html, jsp)
- **Visibility:** Copyright in internal comments only
- **Format:** `<!-- ... -->` or `<%-- ... --%>` comments
- **Purpose:** Legal protection without cluttering user interface
- **Important:** Copyright NOT displayed in rendered HTML

---

## Automated Management

### Pre-Commit Hook

The repository includes a pre-commit hook that automatically runs the unified management script before each commit.

**Location:** `.git/hooks/pre-commit`

**What it does:**
1. Runs `manage-copyrights.sh`
2. Updates copyrights for modified files
3. Adds copyrights to new files
4. Stages any copyright changes
5. Continues with commit if successful

**To disable temporarily:**
```bash
git commit --no-verify
```

---

## Best Practices

### 1. Let the Hook Handle It
The pre-commit hook automatically manages copyrights. You usually don't need to run the script manually.

### 2. New Files
When creating new files:
- The pre-commit hook will add copyright automatically
- Or run `./manage-copyrights.sh` manually

### 3. Year Ranges
- Single year: `2025` (file created and not modified since)
- Year range: `2025-2026` (file created in 2025, modified in 2026)
- The script handles this automatically based on Git history

### 4. File Placement Rules

**Java Files:**
- Copyright AFTER package declaration
- Prevents compilation errors
- Automatically handled by script

**HTML/JSP Files:**
- Copyright in comments (internal)
- NOT visible in rendered output
- Maintains clean user interface

**Shell/Python Scripts:**
- Copyright after shebang line
- Maintains script executability

**XML Files:**
- Copyright after XML declaration
- Maintains XML validity

---

## Troubleshooting

### Copyright Not Updating
**Problem:** File modified but copyright not updated

**Solutions:**
1. Check if file was actually modified this year (Git history)
2. Verify copyright format matches expected pattern
3. Run script manually: `./manage-copyrights.sh`
4. Check Git history: `git log -1 --format="%ad" --date=format:"%Y" -- <file>`

### Compilation Errors in Java
**Problem:** Java compilation fails due to copyright placement

**Solution:**
The unified script automatically places copyright AFTER package declaration. If you have issues:
1. Run `./manage-copyrights.sh`
2. Verify package declaration is on first line
3. Check for extra spaces or formatting issues

### Script Not Found
**Problem:** Cannot find or execute script

**Solutions:**
1. Ensure you're in the repository root
2. Check script exists: `ls -la 06-Resources/tools/manage-copyrights.sh`
3. Make executable: `chmod +x 06-Resources/tools/manage-copyrights.sh`

### Copyright in Wrong Format
**Problem:** Copyright doesn't match expected format

**Solution:**
The script looks for: `© Copyright [year] Olivier Planson`
- Ensure exact format is used
- Run script to standardize format
- Check for typos or extra characters

---

## Manual Copyright Addition

If you need to add copyright manually (not recommended, use script instead):

### Java
```java
/* © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

package com.bank.model;

public class MyClass {
    // ...
}
```

### HTML
```html
<!-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. -->
<!DOCTYPE html>
<html>
    <!-- ... -->
</html>
```

### JSP
```jsp
<%-- © Copyright 2025-2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <!-- ... -->
</html>
```

---

## Verification

To verify copyright consistency:

```bash
# Run the unified script (it will report status)
cd 06-Resources/tools
./manage-copyrights.sh

# Check files without copyright manually
find . -name "*.java" -type f | while read f; do
    if ! grep -q "© Copyright.*Olivier Planson" "$f"; then
        echo "Missing: $f"
    fi
done

# Check files with old year only
find . -name "*.java" -type f | while read f; do
    if grep -q "© Copyright 2025 Olivier Planson" "$f" && ! grep -q "2026" "$f"; then
        echo "Old year: $f"
    fi
done
```

---

## Script Output Interpretation

The unified script provides clear output:

```
✓ Updated: file.java (2025 → 2025-2026)     # File modified this year, copyright updated
✓ Added copyright: newfile.java              # New file, copyright added
⏭️  Already current: file.java (2025-2026)   # Copyright already up to date
⏭️  Not modified this year: file.java        # File not modified, skipped
⚠️  No Git history: file.java                # File not in Git, skipped
```

---

## Summary

### Unified Approach
- **One Script:** `manage-copyrights.sh` handles everything
- **Automated:** Pre-commit hook runs automatically
- **Smart:** Only updates modified files (Git-based)
- **Complete:** Adds copyright to new files
- **Correct:** Proper placement for each file type

### File Categories
1. **Java Files:** Internal code, copyright after package
2. **Source Files:** Internal (sh, py, xml, md), copyright at beginning
3. **User-Visible:** HTML/JSP, copyright in comments (not rendered)

### Workflow
1. Create or modify files
2. Commit changes
3. Pre-commit hook runs automatically
4. Copyrights managed automatically
5. No manual intervention needed

The copyright management system is fully automated and requires no manual intervention in normal workflow.

---

**Last Updated:** January 2026  
**Maintainer:** Olivier Planson  
**License:** All rights reserved. Reproduction prohibited. Made with IBM Bob.