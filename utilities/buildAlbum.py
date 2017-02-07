#!/usr/bin/env python
# -*- coding=utf-8 -*-

import json
#import PIL
import os, sys

#----------------------------------------------------
class Album:
    ''' 
    @param photoFilename the file name with the description of the photos
    @param albumFileName the file name with the description of the album
    '''
    def __init__(self, albumFileName, photoFileName):
        # read the file with the photos
        data = None
        with open(photoFileName) as data_file:    
            data = json.load(data_file)
        self.photos = [ None ] * len(data)
        for p in data:
            self.photos[ p["index"] ] = p 
        # read the file with the album description
        with open(albumFileName) as data_file:    
            self.album = json.load(data_file)

        #print "%d %s" % (self.data[1]["id"], self.data[1]["name"])

    '''
    Create one page of the album
    @param numPage the number fo the page
    @param photoList the ordered list of photo in the page
    '''
    def create_page(self, html_dir, photos_dir, numPage, photoList):
        f = open(os.path.join(html_dir, "%s_%d.ejs" % (self.album["basename"], numPage)), "w")
        self.create_body_page(f, photos_dir, numPage, photoList)
        f.close()

    '''
    Create the bottom of the page 
    @param numPage the number fo the page
    '''
    def create_body_page(self, f, photos_dir, numPage, photoList):
        f.write("<div id=\"album\">\n")
        f.write("<h3>Page %d</h3>\n" % (numPage + 1))

        self.create_navigation(f, numPage)

        f.write("<div id=\"pictures\" style=\"width:%dpx; height:%dpx\">\n" % (self.album["pages"][numPage]["width"], self.album["pages"][numPage]["height"]))
        pos = 0
        for i in photoList:
            photo = self.album["pages"][numPage]["photos"][pos]
            f.write("<img src=\"/<%%= albumName %%>/%s/%s\" width=\"%d\" height=\"%d\">\n" % (photos_dir, self.photos[i]["name"], photo["width"], photo["height"]))
            pos += 1

        f.write("</div>\n") # pictures
        self.create_navigation(f, numPage)

        f.write("</div>\n") # album

    '''
    Create the navigation link in the album
    @param numPage the number of the page
    '''
    def create_navigation(self, f, numPage):
        f.write("<div id=\"navigation\">\n")
        f.write("<nav aria-label=\"Page navigation\">\n")
        f.write("<ul class=\"pagination\">\n")

        if numPage > 0:
            f.write("<li><a href=\"/album/<%%= albumName %%>/%d\" aria-label=\"Previous\"><span aria-hidden=\"true\">&laquo;</span></a></li>\n" % (numPage -1))

        for i in xrange(0, self.album["page"]):
            if(i == numPage):
                f.write("<li class=\"active\">")
            else:
                f.write("<li>")
            f.write("<a href=\"/album/<%%= albumName %%>/%d\">%d</a></li>\n" % (i, i+1))

        if numPage < self.album["page"] - 1:
            f.write("<li><a href=\"/album/<%%= albumName %%>/%d\" aria-label=\"Next\"><span aria-hidden=\"true\">&raquo;</span></a></li>\n" % (numPage + 1))

        f.write("</ul>\n")
        f.write("</nav>\n")
        f.write("</div>")

    ''' 
    Create all the pages of the album 
    @param html_dir
    @param photos_dir
    @param solution_name the order of the photo in the album
    '''
    def create_album(self, html_dir, photos_dir, solution_name):
        # read the solution order
        with open(solution_name) as f:
            photosOrder = [ int(x) for x in f.readline().split() ]

        n = 0
        for i in range(self.album["page"]):
            self.create_page(html_dir, photos_dir, i, photosOrder[n:(n+self.album["pagesize"][i])])
            n = n + self.album["pagesize"][i]

#===================================================================
if __name__ == '__main__':
    album_name     = "../resources/data/info-album-6.json"        # file name of the album information
    photos_name    = "../resources/data/info-photo.json"        # file name of the photo information
    html_dir       = "../www/views/albums/"                        # path to html source files
    photos_dir     = "img"                         # path to images from the html directory
    solution_name  = "../resources/data/chronologic-order.sol"  # (default) file name of the solution which gives the assignement of the photos

    if len(sys.argv) > 1:
        if not os.path.exists(sys.argv[1]):
            print ("File not found: " + sys.argv[1])
            sys.exit()
        else: 
            solution_name = sys.argv[1]

        print (html_dir + sys.argv[2])
        if not os.path.exists(html_dir + sys.argv[2]):
            print ("Album not found: " + sys.argv[2])
            sys.exit()
        else:
            album = Album(album_name, photos_name)
            album.create_album(html_dir + sys.argv[2], photos_dir, solution_name)

            infoFile = open(html_dir + sys.argv[2] + "/info.txt", "w")
            infoFile.write(solution_name)
            infoFile.close()




