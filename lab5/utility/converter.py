from PIL import Image
import csv

input_image_path = "input_img.png"
output_csv_path = "grayscale.csv"

image = Image.open(input_image_path)
image = image.convert("L") # black and white 8-bit
image = image.resize((20, 20), Image.Resampling.LANCZOS);

pixels = list(image.getdata())
# pixels = [pixels[i * 20:(i + 1) * 20] for i in range(20)]
for i in range(len(pixels)):
    pixels[i] = pixels[i] / 255


with open(output_csv_path, mode="w", newline="") as file:
    writer = csv.writer(file)
    writer.writerow(pixels);
    # writer.writerows(im)

print(f"Grayscale CSV saved to {output_csv_path}")
