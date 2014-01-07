nr
==

1. An invaluable source of info about the Network Rail datafeeds is http://nrodwiki.rockshore.net/
2. Sign up at https://datafeeds.networkrail.co.uk/ntrod
3. Once you are signed up, log in at the same page, and go to the My Feeds tab. There, click on Train Movements to the left, and subscribe to All TOCS
4. You also need to sign up for all reference data on the same page
5. Create a directory (nr or similar). Within this directory create a subdirectory nrdata, and check this code out into another subdirectory
6. Download the location information (which is used to map STANOX codes to TipLoc codes and names) from http://datafeeds.networkrail.co.uk/ntrod/SupportingFileAuthenticate?type=CORPUS and unizp it into the nrdata directory
7. Obtain the NaPTAN dataset from http://data.gov.uk/dataset/naptan (get the Zipped CSV format). Store the RailReferences.csv file from this zip in the nrdata direcotry
8. Create a file in the nrdata directory called nr.props. In it you need to store your email and password used to create the datafeed subscription in the form
nrUsername=<EMAIL>
nrPassword=<PASSWORD>
9. Compile the code
10. Assuming that the code has been compiled into a file called nr.jar, run the server from the checkout directory by running 
java -classpath nr.jar:activemq-all-5.9.0.jar:netty-all-4.0.14.Final.jar me.taks.nr.Runner ../nrdata/CORPUSExtract.json ../nrdata/RailReferences.csv

You should now have a running webserver, attached to the nr activeMQ server. You can get it to display the map by accessing 
http://localhost:8080/map
