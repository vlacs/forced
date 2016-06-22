# Forced! 

Forced is a Clojure library that targets the REST APIs provided by Salesforce.

Forced currently focuses on utilizing the rich RESTful API provided by
Salesforce. This API can fully alter data residing in Salesforce as well as
provide descriptions about the data contained within any organization.

## Usage

### 1. Getting started
All Forced instances must be initialized by using the ```forced/start!```
function. This function takes in a map of the following keys:

1. ```auth-endpoint``` which is the OAuth 2.0 endpoint that is going to be used
   for authentication. For a test sandbox this would be ```https://test.salesforce.com/services/oauth2/token```.
2. ```client-id``` is provided by Salesforce.
3. ```client-secret``` is also provided by Salesforce.
4. ```username``` is the username that Forced will be authenticating with.
5. ```password``` is the password for the user mentioned above.

Please note that a security token must be included in the password if the
location you're authenticating from isn't a whitelisted IP address in your
Salesforce instance.

TODO: Finish this README.

## License

Copyright © 2016 VLACS <jdoane@vlacs.org>

Copyright © 2016 Jon Doane <jrdoane@gmail.com>

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
