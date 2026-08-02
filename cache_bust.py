import os
import glob

files = glob.glob('src/main/resources/templates/**/*.html', recursive=True)
for file in files:
    with open(file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Cache bust the main CSS
    new_content = content.replace('/css/main.css', '/css/main.css?v=2.0')
    
    if content != new_content:
        with open(file, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Busted cache in: {file}")
