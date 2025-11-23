<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes" />
  <xsl:param name="title" select="'Catalog'" />
  <xsl:template match="/">
    <html>
      <head><title><xsl:value-of select="$title"/></title></head>
      <body>
        <h1><xsl:value-of select="$title"/></h1>
        <ul>
          <xsl:for-each select="catalog/book">
            <li>
              <b><xsl:value-of select="title"/></b>
              — <i><xsl:value-of select="author"/></i>
              (<xsl:value-of select="price/@currency"/><xsl:text> </xsl:text><xsl:value-of select="price"/>)
            </li>
          </xsl:for-each>
        </ul>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
