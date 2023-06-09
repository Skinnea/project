import uvicorn
from fastapi import FastAPI, UploadFile, File
import tensorflow_hub as hub
import tensorflow as tf
import numpy as np
import os
from tensorflow.keras.preprocessing import image
import shutil
import time
import json

os.environ["TF_CPP_MIN_LOG_LEVEL"] = "3"

# port = int(os.getenv("PORT"))
port = os.getenv("PORT")
if port is None:
  port = 8080

app = FastAPI()  # create a new FastAPI app instance

t = time.time()
export_path = "saved_model/1685796311".format(int(t))
model = tf.keras.models.load_model(
    export_path, custom_objects={"KerasLayer": hub.KerasLayer}
)

Labels = ["Atopic-Dermatitis", "Poison-Ivy", "Scabies-Lyme"]


def predict(img):
    img = tf.io.read_file(img)  # Read the image file
    img = tf.image.decode_image(img, channels=3)  # Decode the image
    img = tf.image.resize(img, (224, 224))  # Resize the image to (224, 224)
    img = img / 255.0  # Normalize
    probabilities = model.predict(np.asarray([img]))[0]
    class_idx = np.argmax(probabilities)

    result = {
        "result": Labels[class_idx],
        "accuracy": f"{float(probabilities[class_idx])*100:.2f}%"
    }

    if Labels[class_idx] == "Atopic-Dermatitis":
        result ["deskripsi"] = "Dermatitis Atopik adalah sebuah penyakit kulit yang ditandai dengan gejala seperti kulit kering, gatal secara terus-menerus, dan ruam merah di kulit."
        result ["img_obat"] = "https://images.tokopedia.net/img/cache/900/hDjmkQ/2022/4/26/c1b483ca-ca76-4abf-8b84-a57d76b0d3b8.jpg"
        result ["nama_obat"] = "Atopic Salicylic Acid"
        result ["pemakaian_obat"] = "2 - 3x sehari"
        result ["detail_obat"] = "Beprosalic merupakan jenis obat yang digunakan dalam pengobatan penyakit kulit misalnya seperti inflamasi psoriasis dan juga seborrhea pada kulit kepala lesi psoriasis dan juga dermatosis pada kortikosteroid, zat aktif yang terkandung di dalam obat ini adalah betamethasone dipropionate yang merupakan jenis obat masuk ke dalam golongan kortikosteroid dan juga salicylic acid yang merupakan obat topical untuk mengobati penyakit dermatitis seboroik, jerawat, psoriasis, kapalan, keratosis pilaris, acanthosis nigricans, ichthyosis dan juga kutil."
        # informasi untuk Atopic-Dermatitis

    elif Labels[class_idx] == "Poison-Ivy":
        result ["deskripsi"] = "Poison ivy adalah kondisi di mana kulit mengalami reaksi alergi yang disebabkan oleh sebuah zat resin yang bernama “urushiol” pada dedaunan, batang, dan akar Ivy."
        result ["img_obat"] = "https://images.tokopedia.net/img/cache/900/VqbcmM/2023/5/25/743039ad-9276-4b3e-8cb0-f4024014f3b3.jpg"
        result ["nama_obat"] = "Prosix"
        result ["pemakaian_obat"] = "2 - 3x sehari"
        result ["detail_obat"] = "Menyembuhkan semua gejala alergi baik alergi makanan, udara maupun alergi debu dan lain2. Gatal2 alergi, meler alergi, batuk alergi, bersin2 alergi dapat cepat disembuhkan dengan minum 1 tablet sehari. Bisa dikonsumsi anak dan dewasa."
        # informasi untuk Poison-Ivy

    elif Labels[class_idx] == "Scabies-Lyme":
        result ["deskripsi"] = "Scabies Lyme adalah penyakit akibat infeksi bakteri yang ditularkan melalui gigitan kutu."
        result ["img_obat"] = "https://images.tokopedia.net/img/cache/500-square/VqbcmM/2022/12/13/3a1d8af6-d8bd-4ffd-ae74-638d973b36d6.jpg.webp?ect=4g"
        result ["nama_obat"] = "Scabies Demodex"
        result ["pemakaian_obat"] = "2 - 3x sehari"
        result ["detail_obat"] = "Spray obat kulit & anti kutu hewan alami. Efektif mengobati semua gangguan kulit dan masalah bulu hewan kesayangan Anda serta menjauhkan hewan kesayangan dari serangan kutu yang berbahaya. Dibuat oleh dokter hewan spesialis dengan menggunakan bahan herbal / alami sehingga sangat aman untuk hewan kesayangan Anda."
        # informasi untuk Scabies-Lyme

    return result

@app.get("/")
def hello_world():
    return "hello world"

@app.post("/predict")
def classify(input: UploadFile = File(...)):
    savefile = input.filename
    with open(savefile, "wb") as buffer:
        shutil.copyfileobj(input.file, buffer)
    result = predict(savefile)
    os.remove(savefile)
    return result

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=port, timeout_keep_alive=1200)
