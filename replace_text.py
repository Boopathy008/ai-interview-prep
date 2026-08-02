import os
import glob

replacements = {
    'Dashboard': 'Overview',
    'Start Practice': 'Begin Practice',
    'Mock Interview': 'Interview Practice',
    'Mock Interviews': 'Interview Practices',
    'My Progress': 'Progress',
    'Study Plan': 'Learning Plan',
    'Recent Tests': 'Recent Activity',
    'Quick Start': 'Continue Practice',
    "Start Today's Practice": "Start Now"
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
        print(f"Updated: {file}")
