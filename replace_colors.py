import os
import glob
import re

replacements = {
    '#6C63FF': 'var(--primary)',
    '#00D8A0': 'var(--secondary)',
    '#FF6B6B': 'var(--accent)',
    '#FFB74D': 'var(--warning)',
    '#29B6F6': 'var(--info)',
    'rgba(108,99,255,0.2)': 'var(--primary-light)',
    'rgba(0,216,160,0.15)': 'rgba(20,184,166,0.15)',
    'rgba(255,183,77,0.15)': 'rgba(245,158,11,0.15)',
    '#00A87A': 'var(--success)',
    '#E8FBF5': 'rgba(34, 197, 94, 0.1)', # success light
    '#FFF3E0': 'rgba(245, 158, 11, 0.1)', # warning light
    '#FEF0F0': 'rgba(139, 92, 246, 0.1)', # accent light
    '#00875A': 'var(--success)',
    '#B45309': 'var(--warning)',
    '#B91C1C': 'var(--accent)'
}

files = glob.glob('src/main/resources/templates/**/*.html', recursive=True)
for file in files:
    with open(file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    original_content = content
    for old, new in replacements.items():
        content = content.replace(old, new)
    
    if original_content != content:
        with open(file, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Updated colors in: {file}")
