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
                <xsl:value-of select="NolClass"/>
                <style> table.nol-table, .nol-table td { border: 1px solid black; border-collapse: collapse; } </style>
                <table class="nol-table">
                <tr>
                <th>Place</th>
                <th>Name</th>
                <th>Total</th>
                <xsl:for-each select="NolEventList/Event">
                <th><div title="{Name}"><xsl:value-of select="RaceNumber"/></div></th>      
                </xsl:for-each>                   
                </tr>
 
                <xsl:for-each select="NolPersonResult">                    
                    <tr>
                    <td><xsl:value-of select="Place"/></td>
                    <td><xsl:value-of select="Name"/></td>
                    <td><xsl:value-of select="Total"/></td>    
                    <xsl:for-each select="Result">                    
                        <td><xsl:value-of select="Score"/></td>
                    </xsl:for-each>                      
                    </tr>
                    </xsl:for-each>        
                </table>
                <br/>
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
