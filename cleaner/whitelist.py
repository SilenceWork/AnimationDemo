#! /usr/bin/python
# -*- coding: UTF-8 -*-

'''

yjy cleaner

白名单处理类
'''
__metaclass__ = type 

import sys

class WhiteList:
	"  读取白名单文件white，在白名单内的资源文件不会被清除 " 
	
	def __init__(self,white):
		self.list = []
		white_file = open(white,'a+')
		white_file.seek(0)
		print "白名单："
		try:
			for line in white_file:
				print "  --",line.strip()
				self.list.append(line.strip())
			print ''
		finally:
			white_file.close()

	def isWhite(self,name):
		return name in self.list


if(__name__ == "__main__"):

	if len(sys.argv) == 1:
		white_list = WhiteList("white.list")
	elif len(sys.argv) == 2:
		white_list = WhiteList(sys.argv[1])

	name = 'icon_camera_develop_picture.png'
	white_list.isWhite(name)		
	


