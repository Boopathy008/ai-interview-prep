import os

file_path = 'src/main/resources/templates/index.html'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace hardcoded colors with variables or updated hex codes
replacements = {
    'body { background: #0F0F2D; }': 'body { background: var(--bg); }',
    'background: rgba(15,15,45,0.8);': 'background: rgba(11, 16, 32, 0.85);',
    'background: #12122A;': 'background: var(--bg-card);',
    'color: #6C63FF;': 'color: var(--primary);',
    'background: #0F0F2D;': 'background: var(--bg);',
    'background: rgba(108,99,255,0.08);': 'background: var(--primary-light);',
    'border-color: rgba(108,99,255,0.3);': 'border-color: var(--border-focus);',
    'background: linear-gradient(135deg, #6C63FF, #00D8A0);': 'background: linear-gradient(135deg, var(--primary), var(--secondary));',
    'color: #00D8A0;': 'color: var(--secondary);'
}

for old, new in replacements.items():
    content = content.replace(old, new)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print(f"Updated: {file_path}")
