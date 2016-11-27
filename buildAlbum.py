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
        f = open(os.path.join(html_dir, "%s_%d.html" % (self.album["basename"], numPage)), "w")
        self.create_header_page(f, numPage)
        self.create_body_page(f, photos_dir, numPage, photoList)
        self.create_footer_page(f, numPage)
        f.close()

    '''
    Create the header of the page 
    @param numPage the number of the page
    @param photoList the ordered list of photo in the page
    '''
    def create_header_page(self, f, numPage):
        f.write("<html>\n")

        f.write("<header>\n")
        f.write("<title>Mon album</title>\n")
        f.write("<link rel=\"stylesheet\" media=\"screen\" type=\"text/css\" title=\"\" href=\"styleAlbum.css\" />\n")
        f.write("</header>\n")

        f.write("<body>\n")
        f.write("<div id=\"main\"/>\n")        
        f.write("<div id=\"titre\">\n")
        f.write("<h1>Mon album de r&ecirc;ve</h1>\n")
        f.write("</div>\n")
        f.write("\n")

    '''
    Create the bottom of the page 
    @param numPage the number fo the page
    '''
    def create_body_page(self, f, photos_dir, numPage, photoList):
        f.write("<div id=\"album\">\n")
        f.write("<h3>page %d</h3>\n" % (numPage + 1))
        f.write("<a name=\"photo\">\n")

        self.create_navigation(f, numPage)

        f.write("<div id=\"pictures\">\n")
        pos = 0
        for i in photoList:
            photo = self.album["pages"][numPage]["photos"][pos]
            f.write("<img src=\"%s/%s\" width=\"%d\" height=\"%d\">"% (photos_dir, self.photos[i]["name"], photo["width"], photo["height"]))
            pos += 1

        f.write("</div>\n") # pictures
        self.create_navigation(f, numPage)

        f.write("</div>\n") # album

    '''
    Create the navigation link in the album
    @param numPage the number of the page
    '''
    def create_navigation(self, f, numPage):
        f.write("<div class=\"navigation\">\n")            
        f.write("<a href=\"%s_%d.html#photo\">first</a>" "" % (self.album["basename"], 0))
        if numPage > 0:
            f.write("<a class=\"apres\" href=\"%s_%d.html#photo\">prev</a>" "" % (self.album["basename"], numPage-1))
        else:
            f.write("<a class=\"apres\" href=\"%s_%d.html#photo\">prev</a>" "" % (self.album["basename"], numPage))
        if numPage < self.album["page"] - 1:
            f.write("<a class=\"apres\" href=\"%s_%d.html#photo\">next</a>" "" % (self.album["basename"], numPage+1))
        else:
            f.write("<a class=\"apres\" href=\"%s_%d.html#photo\">next</a>" "" % (self.album["basename"], numPage))
        f.write("<a class=\"apres\" href=\"%s_%d.html#photo\">last</a>\n" "" % (self.album["basename"], self.album["page"]-1))
        f.write("</div>\n") # navigation
        
    '''
    Create the body of the page 
    @param photoList the ordered list of photo in the page
    '''
    def create_footer_page(self, f, numPage):
        f.write("</div>\n") # main
        f.write("</body>\n")
        f.write("</html>\n")

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
    album_name     = "data/info-album.json"        # file name of the album information
    photos_name    = "data/info-photo.json"        # file name of the photo information
    html_dir       = "html"                        # path to html source files
    photos_dir     = "img"                         # path to images from the html directory
    solution_name  = "data/chronologic-order.sol"  # (default) file name of the solution which gives the assignement of the photos

    if len(sys.argv) > 1:
        if not os.path.exists(sys.argv[1]):
            print "File not found: " + sys.argv[1]
        else: 
            solution_name = sys.argv[1]

    album = Album(album_name, photos_name)
    
    album.create_album(html_dir, photos_dir, solution_name)

