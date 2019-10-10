# Last FM Tracks History Downloader

Downloads all of your song history from Last.fm into a csv file. 

You get a csv file in the following format: 

    Track Name;Artist;Album;Played Unix Timestamp
    
If rerun, only new data is being downloaded.

# Installation
Build and create settings.properties file in root folder:

    apiKey = YOUR_API_KEY
    username = USERNAME
    saveFile = lastfmdata.csv
    reponseLogFolder = responses
    
**apiKey** Your api key, it can be generated for free on https://www.last.fm/api/account/create and will 
be available immediately

**username** The username you want to scrape data from

**saveFile** Where tracks get saved

**responseLogFolder** For every response you get from the Last.fm API the json response
 is saved in this directory
 
 