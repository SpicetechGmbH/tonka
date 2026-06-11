<template>
    <div
        id="footer"
        class="footer"
    >
        <span>
            <a
                href="https://sgx.geodatenzentrum.de/web_public/gdz/datenquellen/Datenquellen_TopPlusOpen.html"
                target="_blank"
            >
                Kartendarstellung und Präsentationsgraphiken: © Bundesamt für Kartographie und Geodäsie (2022)
            </a>&nbsp;
            <a
                href="https://www.stadtlexikon-stuttgart.de/dts/"
                target="_blank"
            >
                Tiles © Stadtmessungsamt, LHS Stuttgart
            </a>&nbsp;
            <a
                href="https://www.stuttgart.de/datenschutzerklaerung.php"
                target="_blank"
            >Datenschutzerklärung</a>&nbsp;
            <a
                href="https://www.stuttgart.de/impressum"
                target="_blank"
            >Impressum</a>
        </span>
    </div>
</template>

<style lang="scss">
.footer {
    position: fixed;
    right: 0;
    bottom: 0;
    font-size: 10px;
    padding-right: 10px;
}

@media only screen and (max-width: 780px) {
    .footer {
        padding: 10px;
        bottom: 0;
    }
}
</style>
