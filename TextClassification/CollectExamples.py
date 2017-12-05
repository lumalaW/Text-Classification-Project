import os
import xlwt
import csv

def examine_samples(output_file, samples_dir):
	texts_col = []
	labels_col = []
	labels = []

	print("Searching for sub directories...")
	sub_dirs = get_sub_directories(samples_dir)
	for sd in sub_dirs:
		labels.append(sd)
		txt_files = get_text_files_from_dir(samples_dir+"/"+sd)
		print("Found {} text files.".format(len(txt_files)))
		for tf in txt_files:
			text = get_file_text_contents(tf)
			# print("Text: ", text)
			texts_col.append(text)
			labels_col.append(sd)
	print("Writing CSV file {}".format(output_file))
	write_csv_file(output_file, texts_col, labels_col)
	return labels


def get_sub_directories(rootDir):
	dirs = []
	for o in os.listdir(rootDir):
		if(os.path.isdir(os.path.join(rootDir, o))):
			# print("Found sub directory:",o)
			dirs.append(o)
	return dirs

def get_text_files_from_dir(dir):
	print("Searching for Text files in directory ", dir)
	text_files = []
	for root, dirs, files in os.walk(dir):
		for file in files:
			if file.endswith('.txt'):
				# print("Text file ", os.path.join(root, file))
				text_files.append(os.path.join(root, file))
	return text_files

def get_file_text_contents(filepath):
	with open (filepath, "r") as file:
		data=file.read()
		return data


def write_excel_sheet(filename, sheet, textList, labelAssignments):
    book = xlwt.Workbook()
    sh = book.add_sheet(sheet)

    for i in len(textList):	
    	sh.write(i, 0, textList[i])
    	sh.write(i, 1, labelAssignments[i])

    book.save(filename)

def write_csv_file(filename, textList, labelAssignments):
	with open(filename, 'w') as csvfile:
		fieldnames = ['text', 'label']
		writer = csv.DictWriter(csvfile, fieldnames=fieldnames,delimiter=',', lineterminator='\n')

		for i in range(0,len(textList)-1):	
			writer.writerow({'text':textList[i],'label':labelAssignments[i]})