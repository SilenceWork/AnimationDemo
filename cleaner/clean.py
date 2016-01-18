#! /usr/bin/env python
# -*- coding: UTF-8 -*-

'''

yjy cleaner

useage: python clean.py [/path/white.list]
white.list为白名单，可选参数。白名单内的文件将不做处理。
white.list文件每行为一个资源文件名。如：
	close.png
	button_pressed.9.png
	....

'''

import xml.sax
import os
import shutil
from whitelist import *

global index
index = 1

TEMP_DIR="Unused"

def createPath(filePath):
	dir=os.path.split(filePath)[0]
	if not os.path.exists(dir):
		os.makedirs(dir)

def deleteUnusedFile(filePath):
	if os.path.exists(filePath):
		srcPath=filePath[filePath.index("res/"):]
		move_from=filePath
		move_to=TEMP_DIR + "/" + srcPath
		print "	move from ",move_from
		print "	move to ",move_to
		createPath(move_to)
		shutil.move(move_from,move_to)
	else:
		print "error  >>>>"
		print filePath," not exist"

class MovieHandler ( xml.sax.ContentHandler ):

	def __init__(self):
		self.id=""
		self.list = []
		self.start=""

	#start 
	def startElement(self,tag,attributes):
		global index
		self.start = tag
		if tag == "issue":
			id = attributes["id"]
			if id == "UnusedResources":
				self.id = id;
				print "***********find ",index, self.id ," ********"
				index = index + 1
		elif tag == "location" and self.id=="UnusedResources":
			file = attributes["file"]
			if not file in self.list:
				self.list.append(file)
				print "	file path:",file

	#end
	def endElement(self,tag):
		if tag == "issue" and self.id == "UnusedResources":
			self.start =""
			self.id=""
			self.location=""

	#content start
	def characters(self,content):
		pass

if (__name__ == "__main__"):

	print "当前目录:",os.getcwd(),"\n"

	whiteFile = os.getcwd() + "/white.list"
	if len(sys.argv) == 1:
		print "使用默认白名单:",whiteFile,"\n"
	elif len(sys.argv) > 1:
		assert os.path.isabs(sys.argv[1]),"指定白名单文件请使用绝对路径"
		if os.path.isfile(sys.argv[1]):
			whiteFile = sys.argv[1]
			print "\n使用白名单:",whiteFile
		else:
			print "指定白名单不存在，输入continue使用默认白名单继续。其它退出"
			isContinue = raw_input(">>")
			assert isContinue == "continue","白名单异常，选择退出"
			print "\n使用默认白名单：",whiteFile,"\n"

	whitelist = WhiteList(whiteFile)	

	os.chdir("../")
	print "设置运行目录:",os.getcwd(),"\n"

	print 'start clean project >>>>'
	cleanResult = os.popen("gradle clean").read()	
	if cleanResult.find('BUILD SUCCESSFUL') < 0:
		print '	gradle clean error  check your project!!!!!!!!!!!'	
		sys.exit()
	else:
		print 'clean success >>>'

	print 'start execute gradle lint >>>'
	lintResult = os.popen("gradle lint").read()
	if not 'BUILD SUCCESSFUL' in lintResult:
		print '	gradle lint error ! check your project'
		sys.exit()
	else:
		print 'parse lint result >>>'

	outLine = lintResult[lintResult.find('Wrote XML report to file://'):]
	xmlPath=outLine[outLine.find('file://')+7:outLine.find('\n')]
	print "result xml:",xmlPath

	if xmlPath == "":
		print 'lint xml result not found ! check your project'
		sys.exit()

	print 'start parse:',xmlPath,'>>>\n'

	parser = xml.sax.make_parser()
	parser.setFeature(xml.sax.handler.feature_namespaces,0)
	
	Handler = MovieHandler()
	parser.setContentHandler(Handler)
	parser.parse(xmlPath)

	print "\n==============开始处理未使用的资源=============\n"

	wl = []
	dl = []

	for str in Handler.list:
		name=os.path.split(str)[1]
		if whitelist.isWhite(name):
			wl.append(name)
			continue
		else:
			dl.append(name)
			deleteUnusedFile(str)

	print "\n==================处理结果=======================\n"
		
	print len(wl),"个文件在白名单中,不做任何处理"	
	for str in wl:
		print "	未删除：",str

	print ""

	print len(dl),"个文件被删除"	
	for str in dl:
		print "	被删除：",str

	print "\n!!!!!!温馨提示!!!!!!"
	print "资源移除路径：",os.getcwd()+"/"+TEMP_DIR
	print '''**************************************
  请重新编译工程，详细检查项目功能。
  确定没有误删文件后，可以将Unused文件夹中的资源删除。
  如发现误删的文件，可以从Unused中将文件恢复，并将文件名加入白名单。
***************************************
		'''

