#!/usr/bin/env python3
"""Reapply this fork's rebrand after each upstream sync (Universal Files)."""
from pathlib import Path
import re, shutil, sys

ROOT = Path(sys.argv[1] if len(sys.argv) > 1 else '.').resolve()

TEXT_PAIRS = [
    ('me.zhanghai.android.files', 'com.universalfiles.app'),
    ('zhanghai/MaterialFiles', 'yashhasan570/UniversalFiles'),
    ('Material Files', 'Universal Files'),
]
JNI_PAIRS = [
    ('me_zhanghai_android_files', 'com_universalfiles_app'),
    ('me/zhanghai/android/files/', 'com/universalfiles/app/'),
]
SKIP_FILES = {'app/src/main/res/raw/licenses.xml'}  # license notice (attribution) stays
TEXT_EXTS = {'.kt', '.java', '.xml', '.gradle', '.kts', '.md', '.txt', '.pro', '.properties',
             '.toml', '.json', '.yml', '.yaml', '.sh', '.aidl', '.c', '.h', '.cpp', '.hpp',
             '.cc', '.gitignore', '.cfg', '.ini', '.plist', ''}
JNI_EXTS = {'.c', '.h', '.cpp', '.hpp', '.cc'}
DEV_FILES = ['README_zh-CN.md', 'SECURITY.md', 'utils/generate-custom-themes.sh',
             '.github/pull_request_template.md']

README = """# Universal Files

Material Design file manager for Android.

Browse and organize files on your device storage and SD cards, connect to SMB, SFTP, FTP and WebDAV servers, and manage archives. Root access supported, with dark and light themes and custom accent colors.

## Features

- Local device storage and SD cards
- SMB, SFTP, FTP and WebDAV servers
- Root access support
- Dark and light themes with custom accent colors

## Contact

Contact on Telegram: @johnconstantine570

## Sync

`.github/workflows/sync.yml` pulls upstream changes daily and re-applies the Universal Files branding via `scripts/migrate.py`.
"""

PRIVACY = """# Privacy Policy

Universal Files does not collect or transmit any personal data. All file operations happen on your device or on the servers you configure yourself.

Contact on Telegram: @johnconstantine570
"""

SHORT = 'Material Design file manager for Android\n'
FULL = """Universal Files is a Material Design file manager for Android.

- Browse and organize files on device storage and SD cards
- Connect to SMB, SFTP, FTP and WebDAV servers
- Root access support
- Dark and light themes with custom accent colors

Contact on Telegram: @johnconstantine570
"""


def sweep():
    n = 0
    for f in ROOT.rglob('*'):
        if not f.is_file() or '.git' in f.parts or 'scripts' in f.parts:
            continue
        rel = str(f.relative_to(ROOT))
        if rel in SKIP_FILES or rel.startswith('.github/workflows/sync'):
            continue
        if f.suffix.lower() not in TEXT_EXTS and f.name not in ('.gitignore', 'gradlew', 'gradlew.bat'):
            continue
        try:
            s = f.read_text(encoding='utf-8')
        except Exception:
            continue
        o = s
        for a, b in TEXT_PAIRS:
            s = s.replace(a, b)
        if f.suffix.lower() in JNI_EXTS:
            for a, b in JNI_PAIRS:
                s = s.replace(a, b)
        if s != o:
            f.write_text(s, encoding='utf-8')
            n += 1
    return n


def move_packages():
    moves = [('app/src/main/java/me/zhanghai/android/files', 'app/src/main/java/com/universalfiles/app'),
             ('app/src/main/aidl/me/zhanghai/android/files', 'app/src/main/aidl/com/universalfiles/app')]
    for a, b in moves:
        src, dst = ROOT / a, ROOT / b
        if src.exists():
            dst.parent.mkdir(parents=True, exist_ok=True)
            if dst.exists():
                for child in src.iterdir():
                    shutil.move(str(child), str(dst / child.name))
                shutil.rmtree(src, ignore_errors=True)
            else:
                shutil.move(str(src), str(dst))
    for base in ['app/src/main/java/me', 'app/src/main/aidl/me']:
        d = ROOT / base
        if d.exists():
            shutil.rmtree(d, ignore_errors=True)


def strip_firebase():
    bg = ROOT / 'app/build.gradle'
    if bg.exists():
        s = bg.read_text()
        s = re.sub(r'//#ifdef NONFREE\nbuildscript \{.*?//#endif\n', '', s, flags=re.S)
        s = re.sub(r'//#ifdef NONFREE\n\s*firebaseCrashlytics \{.*?//#endif\n', '', s, flags=re.S)
        s = re.sub(r'//#ifdef NONFREE\n\s*implementation platform\(.com\.google\.firebase.*?//#endif\n', '', s, flags=re.S)
        bg.write_text(s)
    ai = ROOT / 'app/src/main/java/com/universalfiles/app/app/AppInitializers.kt'
    if ai.exists():
        s = ai.read_text()
        s = s.replace('    ::initializeFirebase,\n', '')
        s = re.sub(r'private fun initializeFirebase\(\) \{\n.*?\n\}\n\n', '', s, flags=re.S)
        ai.write_text(s)
    nf = ROOT / 'app/src/main/java/com/universalfiles/app/nonfree'
    if nf.exists():
        shutil.rmtree(nf, ignore_errors=True)
    gs = ROOT / 'app/google-services.json'
    if gs.exists():
        gs.unlink()


def fix_about():
    lay = ROOT / 'app/src/main/res/layout/about_fragment.xml'
    if lay.exists():
        lines = lay.read_text().splitlines(keepends=True)
        for idname in ('authorGitHubLayout', 'authorTwitterLayout'):
            if not any(f'@+id/{idname}' in l for l in lines):
                continue
            idx = next(i for i, l in enumerate(lines) if f'@+id/{idname}' in l)
            start = max(i for i in range(idx, -1, -1) if '<LinearLayout' in lines[i])
            depth = 0
            end = None
            for i in range(start, len(lines)):
                depth += lines[i].count('<LinearLayout') - lines[i].count('</LinearLayout>')
                if depth == 0:
                    end = i
                    break
            del lines[start:end + 1]
        lay.write_text(''.join(lines))
    af = ROOT / 'app/src/main/java/com/universalfiles/app/about/AboutFragment.kt'
    if af.exists():
        s = af.read_text()
        s = s.replace('''        binding.authorGitHubLayout.setOnClickListener {
            startActivitySafe(AUTHOR_GITHUB_URI.createViewIntent())
        }
        binding.authorTwitterLayout.setOnClickListener {
            startActivitySafe(AUTHOR_TWITTER_URI.createViewIntent())
        }
''', '')
        s = re.sub(r'    companion object \{.*?\n    \}\n\}', '''    companion object {
        private val GITHUB_URI = Uri.parse("https://github.com/yashhasan570/UniversalFiles")
        private val PRIVACY_POLICY_URI =
            Uri.parse("https://github.com/yashhasan570/UniversalFiles/blob/master/PRIVACY.md")
        private val AUTHOR_TELEGRAM_URI = Uri.parse("https://t.me/johnconstantine570")
    }
}''', s, flags=re.S)
        s = s.replace('AUTHOR_RESUME_URI.createViewIntent()', 'AUTHOR_TELEGRAM_URI.createViewIntent()')
        s = re.sub(r'//#ifdef NONFREE\n(\s*binding\.privacyPolicyLayout\.isVisible = true\n\s*binding\.privacyPolicyLayout\.setOnClickListener \{\n.*?\n\s*\}\n)//#endif\n', r'\1', s, flags=re.S)
        af.write_text(s)
    for sf in (ROOT / 'app/src/main/res').rglob('strings.xml'):
        s = sf.read_text(encoding='utf-8')
        o = s
        s = re.sub(r'(<string name="about_author_title">)[^<]*(</string>)', r'\g<1>Contact\g<2>', s)
        s = re.sub(r'(<string name="about_author_name_title">)[^<]*(</string>)', r'\g<1>Contact on Telegram\g<2>', s)
        s = re.sub(r'\s*<string name="about_author_(github|twitter)_title">[^<]*</string>', '', s)
        if s != o:
            sf.write_text(s, encoding='utf-8')


def rewrite_docs():
    (ROOT / 'README.md').write_text(README)
    (ROOT / 'PRIVACY.md').write_text(PRIVACY)
    for name in DEV_FILES:
        f = ROOT / name
        if f.exists():
            f.unlink()
    fl = ROOT / 'fastlane/metadata/android'
    if fl.exists():
        for d in fl.iterdir():
            if d.is_dir() and d.name != 'en-US':
                shutil.rmtree(d, ignore_errors=True)
        en = fl / 'en-US'
        en.mkdir(parents=True, exist_ok=True)
        (en / 'title.txt').write_text('Universal Files\n')
        (en / 'short_description.txt').write_text(SHORT)
        (en / 'full_description.txt').write_text(FULL)
        for sub in ('changelogs', 'images'):
            sd = en / sub
            if sd.exists():
                shutil.rmtree(sd, ignore_errors=True)


if __name__ == '__main__':
    print('sweep:', sweep(), 'files')
    move_packages()
    strip_firebase()
    fix_about()
    rewrite_docs()
    print('rebrand reapplied')
