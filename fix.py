import os
import glob

files = glob.glob('src/main/resources/templates/**/*.html', recursive=True)
for file in files:
    with open(file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    content = content.replace('<div class="brand-icon">🎯</div>', '<div class="brand-icon">🤖</div>')
    content = content.replace('<div class="logo-icon">🎯</div>', '<div class="logo-icon">🤖</div>')
    content = content.replace('justify-content:center;font-size:22px;">🎯</div>', 'justify-content:center;font-size:22px;">🤖</div>')
    content = content.replace('justify-content:center;font-size:26px;margin:0 auto 16px;">🎯</div>', 'justify-content:center;font-size:26px;margin:0 auto 16px;">🤖</div>')
    content = content.replace('justify-content:center;font-size:16px;flex-shrink:0;">🎯</div>', 'justify-content:center;font-size:16px;flex-shrink:0;">🤖</div>')
    content = content.replace('justify-content:center;font-size:14px;">🎯</div>', 'justify-content:center;font-size:14px;">🤖</div>')
    
    with open(file, 'w', encoding='utf-8') as f:
        f.write(content)
