#! /usr/bin/env python
# -*- coding: UTF-8 -*-

import xml.sax
import os
import shutil
import sys

global index
index = 0

TEMP_DIR="Unused"

def createPath(filePath):
	dir=os.path.split(filePath)[0]
	if not os.path.exists(dir):
		os.makedirs(dir)

def deleteUnusedFile(filePath):
	if os.path.exists(filePath):
		srcPath=filePath[filePath.index("main/"):]
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
		self.start=""
		self.id=""
		self.location=""

	#start 
	def startElement(self,tag,attributes):
		global index
		self.start = tag
		if tag == "issue":
			id = attributes["id"]
			if id == "UnusedResources":
				self.id = id;
				index = index + 1
				print "***********find ",index, self.id ," ********"
		elif tag == "location" and self.id=="UnusedResources":
			file = attributes["file"]
			self.location = file
			print "	file path:",self.location

	#end
	def endElement(self,tag):
		if tag == "issue" and self.id == "UnusedResources":
			print "	move ",self.location," to ./UnusedDir"
			deleteUnusedFile(self.location)
			self.start =""
			self.id=""
			self.location=""
			print ""

	#content start
	def characters(self,content):
		pass

if (__name__ == "__main__"):

	print '=================start clean project======================'
	cleanResult = os.popen("gradle clean").read()	
	if cleanResult.find('BUILD SUCCESSFUL') < 0:
		print '!!!!!!!!!!!!!!gradle clean error  check your project!!!!!!!!!!!'	
		sys.exit()
	else:
		print '===================clean success=================='

	print '=================start execute gradle lint================='
	lintResult = os.popen("gradle lint").read()
	if lintResult.find('BUILD SUCCESSFUL') < 0:
		print 'gradle lint error ! check your project'
		sys.exit()
	else:
		print 'parse lint result'

	outLine = lintResult[lintResult.find('Wrote XML report to file://'):]
	xmlPath=outLine[outLine.find('file://')+7:outLine.find('\n')]
	print "result xml:",xmlPath

	if xmlPath == "":
		print 'lint xml result not found ! check your project'
		sys.exit()

	print 'start parse:',xmlPath,'\n'

	parser = xml.sax.make_parser()
	parser.setFeature(xml.sax.handler.feature_namespaces,0)
	
	Handler = MovieHandler()
	parser.setContentHandler(Handler)
	parser.parse(xmlPath)

	if index == 0:
		print '	/*******所有未引用文件都已删除********/\n'
	else:
		print '	/***成功删除了',index,'个无用文件，文件暂时存放在',TEMP_DIR,'文件夹下。***/\n'
		print '	/***你应该至少执行两次该脚本。程序中有一些资源多层引用，第一次执行只能将上层未引用的资源文件删除.***/\n'
	print '	/***请重新编译工程，并认真检查你的应用。如果发现有资源被误删，可以从',TEMP_DIR,'中找回***/\n'
	print '	/***确认删除无误之前，切勿随意删除',TEMP_DIR,'否则后果自负!!***/\n'

