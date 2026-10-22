import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH

doc = docx.Document()

# Set margins
sections = doc.sections
for section in sections:
    section.top_margin = Inches(1)
    section.bottom_margin = Inches(1)
    section.left_margin = Inches(1)
    section.right_margin = Inches(1)

# Title
title = doc.add_paragraph()
run = title.add_run("CampusConnect — Test Cases Document")
run.font.name = "Calibri"
run.font.size = Pt(24)
run.font.bold = True
run.font.color.rgb = RGBColor(19, 42, 76) # Navy
title.alignment = WD_ALIGN_PARAGRAPH.CENTER

subtitle = doc.add_paragraph()
sub_run = subtitle.add_run("Academic Assessment Documentation\nApp Name: CampusConnect | Platform: Android (Jetpack Compose & Firebase)")
sub_run.font.name = "Calibri"
sub_run.font.size = Pt(12)
sub_run.font.italic = True
sub_run.font.color.rgb = RGBColor(118, 114, 104) # Grey
subtitle.alignment = WD_ALIGN_PARAGRAPH.CENTER

doc.add_paragraph()

# Read TEST_CASES.md and parse into Word doc
with open("TEST_CASES.md", "r", encoding="utf-8") as f:
    lines = f.readlines()

for line in lines:
    line_s = line.strip()
    if line_s.startswith("# "):
        p = doc.add_paragraph()
        r = p.add_run(line_s[2:])
        r.font.name = "Calibri"
        r.font.size = Pt(18)
        r.font.bold = True
        r.font.color.rgb = RGBColor(19, 42, 76)
    elif line_s.startswith("## "):
        p = doc.add_paragraph()
        r = p.add_run(line_s[3:])
        r.font.name = "Calibri"
        r.font.size = Pt(14)
        r.font.bold = True
        r.font.color.rgb = RGBColor(242, 169, 59)
    elif line_s.startswith("### "):
        p = doc.add_paragraph()
        r = p.add_run(line_s[4:])
        r.font.name = "Calibri"
        r.font.size = Pt(12)
        r.font.bold = True
        r.font.color.rgb = RGBColor(19, 42, 76)
    elif line_s.startswith("* "):
        p = doc.add_paragraph(style='List Bullet')
        r = p.add_run(line_s[2:])
        r.font.name = "Calibri"
        r.font.size = Pt(11)
    elif line_s.startswith("- "):
        p = doc.add_paragraph(style='List Bullet')
        r = p.add_run(line_s[2:])
        r.font.name = "Calibri"
        r.font.size = Pt(11)
    elif line_s and not line_s.startswith("---"):
        p = doc.add_paragraph()
        r = p.add_run(line_s)
        r.font.name = "Calibri"
        r.font.size = Pt(11)

doc.save("TEST_CASES.docx")
print("Successfully generated TEST_CASES.docx")
