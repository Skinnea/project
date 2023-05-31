# import library
from flask import Flask, request
from flask_restful import Resource, Api
from flask_cors import CORS

# inisiasi object flask
app = Flask(__name__)

# inisiasi object flask_restful
api = Api(app)

# inisiasi object flask_cors
CORS(app)

# inisiasi variabel kosong bertipe dictionary
status = {}  # variabel global, dictionary = json

# membuat class Resource


class ContohResource(Resource):
    # membuat method get dan post
    def get(self):
        # response = {"msg": "Hello World, this is my first restfull app"}
        return status

    def post(self):
        query = request.form["query"]
        kondisi = request.form["kondisi"]
        status["query"] = query
        status["kondisi"] = kondisi
        response = {"msg": "Data berhasil dimasukan"}
        return response


# setup resource
api.add_resource(ContohResource, "/api", methods=["GET","POST"])

if __name__ == "__main__":
    app.run(debug=True, port=5005)
