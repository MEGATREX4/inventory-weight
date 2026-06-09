import os

def create_missing_files(list_file='path.txt'):
    if not os.path.exists(list_file):
        print(f"Помилка: Файл {list_file} не знайдено в цій директорії!")
        return

    with open(list_file, 'r', encoding='utf-8') as f:
        paths = [line.strip() for line in f if line.strip()]

    for path in paths:
        # Визначаємо папку, де має лежати файл
        directory = os.path.dirname(path)

        # Якщо папки немає, створюємо її
        if directory and not os.path.exists(directory):
            os.makedirs(directory, exist_ok=True)
            print(f"Створено директорію: {directory}")

        # Створюємо сам файл, якщо його немає
        if not os.path.exists(path):
            with open(path, 'w', encoding='utf-8') as file:
                pass  # Просто створюємо порожній файл
            print(f"Створено файл: {path}")

if __name__ == '__main__':
    create_missing_files()