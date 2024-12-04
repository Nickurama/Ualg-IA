import csv

input_csv_path = "grayscale.csv"

with open(input_csv_path, mode="r+", newline="") as file:
    reader = csv.reader(file)
    rows = list(reader)
    data = []
    for i in range(len(rows[0])):
        data.append(1 - float(rows[0][i]))

    file.seek(0)
    writer = csv.writer(file)
    writer.writerow(data);
    file.truncate()
