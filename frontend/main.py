from flask import Flask, render_template, request, redirect

app = Flask(__name__)


@app.route('/')
def main():
    return render_template('authorization/register.html')


app.run(debug=True, port=1488)
