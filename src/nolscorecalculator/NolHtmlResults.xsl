<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : NolHtmlResults.xsl
    Created on : 24 February 2017, 4:12 PM
    Author     : shep
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>NolHtmlResults</title>
            </head>
            <body>
                <xsl:for-each select="NolResultList/NolClassResult">
                <table border="1">
                <tr>
                <th><xsl:value-of select="NolClass"/></th>
                <th>Score</th>
                </tr>
 
                <xsl:for-each select="NolPersonResult">                    
                    <tr>
                    <td><xsl:value-of select="Name"/></td>    
                    <xsl:for-each select="NolPersonRaceResult">                    
                        <td><xsl:value-of select="Score"/></td>
                    </xsl:for-each>                      
                    </tr>
                    </xsl:for-each>        
                </table>
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
