from glob import glob
import os
import xlwt

def exampine_samples(output_file, samples_dir):
	texts_col = []
	labels_col = []
	labels = []

	sheet_name='Sheet 1'

	sub_dirs = get_sub_directories(samples_dir):
	for sd in sub_dirs:
		labels.append(sd)
		txt_files = get_text_files_from_dir(samples_dir+"/"+sd)
		for tf in txt_files:
			text = get_file_text_contents(samples_dir+"/"+sd+"/"+tf)
			texts_col.append(text)
			labels_col.append(sd)
	write_excel_sheet(output_file, sheet_name, texts_col, labels_col)
	return labels


def get_sub_directories(rootDir):
	dirs = []
	for o in os.listdir(rootDir):
		dirs.append(o)
	return dirs

def get_text_files_from_dir(dir):
	files = []
	for file in os.listdir(dir):
		if file.endswith(".txt"):
        	files.append(file)
    return files

def get_file_text_contents(filepath):
	with open (filepath, "r") as file:
		data=file.read()
		return data


def write_excel_sheet(filename, sheet, textList, labelList):
    book = xlwt.Workbook()
    sh = book.add_sheet(sheet)

    for i in len(textList):	
    	sh.write(i, 0, textList[i])
    	sh.write(i, 1, labelList[i])

    book.save(filename)