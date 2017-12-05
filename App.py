from flask import Flask, render_template, request
import json

from TextClassification.Classify import classify, train_model
import TextClassification.CollectExamples as collector

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

@app.route('/classifyText', methods=['GET'])
def classifyText():
	text = request.args.get('text')
	print("TEXT = ", text)
	ret = {}
	if(len(text)==0):
		print("ERROR")
		ret['status']='failed'
		ret['message']='text parameter is required'
		return json.dumps(ret)
	category = classify(text)
	print("Classification done: ", category)
	ret['status']='success'
	ret['message']='Classification successful'
	ret['label']=category
	return json.dumps(ret)


@app.route('/train', methods=['POST'])
def train():
	examples_dir = request.args.get('examples_dir')
	target_data_csv = './data.csv'
	target_labels_csv = './labels.csv'
	labels = collector.examine_samples(examples_dir, target_data_csv, target_labels_csv)

	ret = {}
	if(len(labels)==0):
		ret['status']='failed'
		ret['message']='Unable to parse sample documents. Training aborted.'
		return json.dumps(ret)
	message = train_model(target_data_csv, target_labels_csv)
	ret['labels']=labels
	ret['message']=message
	return json.dumps(ret)

if __name__ == "__main__":
    app.run(debug=True)
