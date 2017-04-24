#!/usr/bin/env python

import os, sys

def tag_info(tagfilename):
    with open(tagfilename, 'rb') as f:
        l = pickle.load(f)

    res = [ ]
    for elem in l:
        res.append({'id': elem['id'], 'classes': elem['results'][0]['result']['tag']['classes'], 'probs': elem['results'][0]['result']['tag']['probs']})
    return res

#===================================================================
if __name__ == '__main__':

    if len(sys.argv) > 3:

        path = sys.argv[1] + "/img"
        tagfile = sys.argv[1] + "/taglist.pkl"
        scorefile = sys.argv[1] + "/score_photo.dat"
        fileoutname = sys.argv[1] + "/info-photo.json"

        ids = index_list(path)

        l = open_images(path, ids)

        jsoninfo = infos(l, tagfile, scorefile)

        with open(fileoutname, "w") as f:
            f.write(jsoninfo)
    else:
        print("No path found")
        sys.exit(0)
