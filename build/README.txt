This is the README file for the Helma build files as part of the Helma Object Publisher.

PREREQUISITES
=============

For building Helma you need Apache Maven. 
For more information about Maven and the Download, see <http://maven.apache.org/>.

For checking out the source files got to <https://github.com/ffppmm/helma.git>.
Current branch is helma_1.8 


STARTING BUILD
==============

The syntax to build an create a target dir with all needed files:

mvn clear package dependency:copy-dependencies

Maven will do all other things, like copying required libraries. Thats it.
