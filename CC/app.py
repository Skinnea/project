from flask import Flask, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

Test = [
    {
        "id": "sehat",
        "title": u"Pertahankan kesehatan anda",
        "description": u"Anda sudah sehat",
        
    },
    {
        "id": "tidak sehat",
        "title": "saran nama obat kulit",
        "URL" : "contoh url",
        "description": "kulit anda tidak sehat",
        
    },
]


@app.route("/check")
def hello():
    return "Hello, world"


@app.route("/json", methods=["GET"])
def get_Test():
    return jsonify({"tasks": Test})