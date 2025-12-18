# Markdown to PowerPoint Conversion Guide
## Using Marp, Slidev, and Pandoc

**Purpose:** Convert course Markdown slides to PowerPoint presentations  
**Tools:** Marp CLI, Slidev, Pandoc  
**Output:** Professional PowerPoint (.pptx) files

---

## 📋 Overview

This guide explains how to convert the course's Markdown lecture files into PowerPoint presentations using three different tools. Each tool has its strengths, and you can choose based on your needs.

---

## 🎨 Option 1: Marp CLI (Recommended)

### Why Marp?
- ✅ Specifically designed for presentations
- ✅ Simple syntax and great defaults
- ✅ Excellent PowerPoint export
- ✅ Supports themes and custom CSS
- ✅ Fast conversion

### Installation

**Using npm (Node.js required):**
```bash
npm install -g @marp-team/marp-cli
```

**Using Homebrew (macOS):**
```bash
brew install marp-cli
```

**Verify installation:**
```bash
marp --version
```

### Basic Usage

**Convert single lecture:**
```bash
cd esipe-javaee/02-Lectures
marp 01-intro-jakartaee.md -o ../slides/01-intro-jakartaee.pptx
```

**Convert all lectures:**
```bash
cd esipe-javaee/02-Lectures
marp *.md -o ../slides/
```

**With custom theme:**
```bash
marp 01-intro-jakartaee.md --theme custom-theme.css -o ../slides/01-intro-jakartaee.pptx
```

### Marp Markdown Syntax

Our lecture files already use Marp syntax:

```markdown
---
marp: true
theme: default
paginate: true
backgroundColor: #fff
---

# Slide Title
Content here

---

## Next Slide
More content
```

### Custom Theme (Optional)

Create `custom-theme.css`:

```css
/* @theme custom */

@import 'default';

section {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
}

h1 {
    color: #FFD700;
    border-bottom: 3px solid #FFD700;
}

h2 {
    color: #FFA500;
}

code {
    background: rgba(255, 255, 255, 0.1);
    color: #FFD700;
}
```

Use it:
```bash
marp 01-intro-jakartaee.md --theme custom-theme.css -o output.pptx
```

---

## 🚀 Option 2: Slidev

### Why Slidev?
- ✅ Modern and interactive
- ✅ Vue.js powered
- ✅ Live preview during editing
- ✅ Rich component ecosystem
- ✅ Great for demos

### Installation

```bash
npm install -g @slidev/cli
```

### Usage

**Start presentation server:**
```bash
cd esipe-javaee/02-Lectures
slidev 01-intro-jakartaee.md
```

This opens a browser with live preview at `http://localhost:3030`

**Export to PowerPoint:**
```bash
slidev export 01-intro-jakartaee.md --format pptx --output ../slides/01-intro-jakartaee.pptx
```

**Export all lectures:**
```bash
for file in *.md; do
    slidev export "$file" --format pptx --output "../slides/${file%.md}.pptx"
done
```

### Slidev Features

**Add presenter notes:**
```markdown
---
# Slide content

<!--
These are presenter notes
Only visible in presenter mode
-->
```

**Add animations:**
```markdown
---
# Animated List

<v-clicks>

- First item appears
- Then second
- Finally third

</v-clicks>
```

---

## 📄 Option 3: Pandoc

### Why Pandoc?
- ✅ Universal document converter
- ✅ Highly customizable
- ✅ Works with any Markdown
- ✅ No special syntax required

### Installation

**macOS:**
```bash
brew install pandoc
```

**Linux:**
```bash
sudo apt-get install pandoc
```

**Windows:**
Download from: https://pandoc.org/installing.html

### Basic Usage

**Convert single file:**
```bash
pandoc 01-intro-jakartaee.md -o ../slides/01-intro-jakartaee.pptx
```

**With custom reference document:**
```bash
pandoc 01-intro-jakartaee.md \
    --reference-doc=template.pptx \
    -o ../slides/01-intro-jakartaee.pptx
```

**Convert all lectures:**
```bash
cd esipe-javaee/02-Lectures
for file in *.md; do
    pandoc "$file" -o "../slides/${file%.md}.pptx"
done
```

### Create Custom Template

1. **Generate default template:**
```bash
pandoc -o template.pptx --print-default-data-file reference.pptx > template.pptx
```

2. **Customize in PowerPoint:**
   - Open `template.pptx`
   - Modify master slides
   - Change colors, fonts, layouts
   - Save

3. **Use template:**
```bash
pandoc lecture.md --reference-doc=template.pptx -o output.pptx
```

---

## 🎯 Comparison Table

| Feature | Marp | Slidev | Pandoc |
|---------|------|--------|--------|
| **Ease of Use** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **PowerPoint Quality** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Customization** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Live Preview** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ❌ |
| **Speed** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Learning Curve** | Low | Medium | Medium |

**Recommendation:** Use **Marp** for quick, professional conversions.

---

## 🔧 Batch Conversion Script

### Convert All Lectures (Marp)

Create `convert-all.sh`:

```bash
#!/bin/bash

# Create output directory
mkdir -p esipe-javaee/slides

# Convert all lecture files
echo "Converting lectures to PowerPoint..."
cd esipe-javaee/02-Lectures

for file in *.md; do
    echo "Converting $file..."
    marp "$file" -o "../slides/${file%.md}.pptx"
done

echo "Conversion complete! Slides saved in esipe-javaee/slides/"
```

Make executable and run:
```bash
chmod +x convert-all.sh
./convert-all.sh
```

### Windows Batch Script

Create `convert-all.bat`:

```batch
@echo off
mkdir esipe-javaee\slides

cd esipe-javaee\02-Lectures

for %%f in (*.md) do (
    echo Converting %%f...
    marp "%%f" -o "..\slides\%%~nf.pptx"
)

echo Conversion complete!
pause
```

---

## 🎨 Styling Tips

### Marp Themes

**Built-in themes:**
- `default` - Clean and simple
- `gaia` - Modern with gradients
- `uncover` - Minimalist

**Use theme:**
```markdown
---
marp: true
theme: gaia
---
```

### Custom CSS in Markdown

```markdown
---
marp: true
---

<style>
section {
    background: #1a1a1a;
    color: #ffffff;
}

h1 {
    color: #00ff00;
}
</style>

# Your Slide
```

### Image Sizing

```markdown
![width:500px](diagram.png)
![height:300px](chart.png)
![bg right:40%](background.jpg)
```

---

## 📊 Handling Mermaid Diagrams

### Option 1: Pre-render Mermaid

Install mermaid-cli:
```bash
npm install -g @mermaid-js/mermaid-cli
```

Convert diagrams to images:
```bash
mmdc -i diagram.mmd -o diagram.png
```

Update Markdown:
```markdown
![Architecture Diagram](diagram.png)
```

### Option 2: Use Marp with Mermaid Plugin

Install plugin:
```bash
npm install -g @marp-team/marp-cli mermaid
```

Diagrams will be automatically rendered.

### Option 3: Screenshot from Slidev

Slidev renders Mermaid natively. Take screenshots and insert into PowerPoint.

---

## ✅ Quality Checklist

Before delivering slides, verify:

- [ ] All slides converted successfully
- [ ] Code syntax highlighting works
- [ ] Images display correctly
- [ ] Mermaid diagrams are visible
- [ ] Tables are formatted properly
- [ ] Links are preserved
- [ ] Fonts are embedded
- [ ] Slide numbers appear
- [ ] No content overflow
- [ ] Consistent styling throughout

---

## 🐛 Troubleshooting

### Issue: Mermaid diagrams not showing

**Solution:**
```bash
# Pre-render diagrams
mmdc -i diagram.mmd -o diagram.png

# Or use Slidev which supports Mermaid natively
slidev export lecture.md --format pptx
```

### Issue: Code blocks not highlighted

**Solution:**
Ensure language is specified:
```markdown
```java
public class Example {
    // code
}
\`\`\`
```

### Issue: Images too large/small

**Solution:**
Use size attributes:
```markdown
![width:600px](image.png)
```

### Issue: Fonts not embedded

**Solution:**
In PowerPoint:
1. File → Options → Save
2. Check "Embed fonts in the file"
3. Select "Embed all characters"

---

## 📚 Additional Resources

### Documentation
- [Marp Documentation](https://marpit.marp.app/)
- [Slidev Documentation](https://sli.dev/)
- [Pandoc Manual](https://pandoc.org/MANUAL.html)

### Tutorials
- [Marp Tutorial](https://www.youtube.com/watch?v=EzQ-p41wNEE)
- [Slidev Guide](https://sli.dev/guide/)

### Themes and Templates
- [Marp Themes](https://github.com/marp-team/marp-core/tree/main/themes)
- [Slidev Themes](https://sli.dev/themes/gallery.html)

---

## 🎓 Best Practices

### For Instructors

1. **Keep slides simple:** One concept per slide
2. **Use visuals:** Diagrams over text
3. **Consistent formatting:** Use same style throughout
4. **Test conversions:** Always preview before class
5. **Backup formats:** Keep both Markdown and PowerPoint

### For Students

1. **Review Markdown:** Easier to read and search
2. **Use PowerPoint:** For presentations and printing
3. **Take notes:** Add to Markdown files
4. **Share feedback:** Report conversion issues

---

## 🚀 Quick Start Commands

**Install Marp:**
```bash
npm install -g @marp-team/marp-cli
```

**Convert one lecture:**
```bash
cd esipe-javaee/02-Lectures
marp 01-intro-jakartaee.md -o ../slides/01-intro-jakartaee.pptx
```

**Convert all lectures:**
```bash
cd esipe-javaee/02-Lectures
marp *.md -o ../slides/
```

**Preview in browser:**
```bash
marp -s 01-intro-jakartaee.md
```

---

## 📝 Summary

- **Marp:** Best for quick, professional conversions
- **Slidev:** Best for interactive presentations with live preview
- **Pandoc:** Best for maximum customization and flexibility

**Recommended workflow:**
1. Write lectures in Markdown
2. Preview with Slidev during development
3. Convert to PowerPoint with Marp for distribution
4. Customize PowerPoint if needed

---

**Happy presenting! 🎉**