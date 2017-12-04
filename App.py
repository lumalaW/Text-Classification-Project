from flask import Flask, render_template, request

from TextClassification.Classify import classify

app = Flask(__name__)
app.config['SECRET_KEY'] = 'TheSecretKey'


@app.route('/', methods=['GET', 'POST'])
def index():
    if request.method == "POST":
        text = request.form['textForClassification']
        entered_text = text
        print(entered_text)
        category = classify(text)
        return render_template('classifiedText.html', text=category, entered_text=entered_text)

    return render_template("index.html")

if __name__ == "__main__":
    app.run(debug=True)
