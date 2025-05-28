<template>
  <v-container class="d-flex justify-center align-center">
    <v-card class="about-help mx-auto">
      <template v-slot:prepend>
        <span class="about-help__title-left"> Hilfeseite </span>
      </template>
      <template #append>
        <a
          href="[eigene_url_eintragen]"
          class="about-help__title-right"
        >
          <span> Zum Lexikon </span>
        </a>
      </template>
      <v-card-text>
        <h2>Funktionalitäten</h2>
        <ul>
          <li><a href="#artikel">Artikel</a></li>
          <li><a href="#suche">Suche</a></li>
          <li><a href="#karten">Karten</a></li>
          <li><a href="#zeitleiste">Zeitleiste</a></li>
        </ul>
        <h2><a href="#faq">FAQs</a></h2>
        <h2>Funktionalitäten</h2>
        <hr>
        <h3 id="artikel">Artikel</h3>
        <p> Das Lexikon enthält Artikel zu Personen <img
            src="@/assets/icons/icon_person.png"
            width="20"
            height="20"
          >, zu Orten <img
            src="@/assets/icons/icon_place.png"
            width="20"
            height="20"
          >, zu Ereignissen <img
            src="@/assets/icons/icon_event.png"
            width="20"
            height="20"
          >, zu Themen <img
            src="@/assets/icons/icon_thema.png"
            width="20"
            height="20"
          > und zu Institutionen <img
            src="@/assets/icons/icon_institution.png"
            width="20"
            height="20"
          >. Sie können Artikel über die Karte, über die <a href="#suchfeld">Suche</a>, oder über die <a href="#zeitleiste">Zeitleiste</a> ansteuern. Haben Sie einen Suchbegriff in das Suchfeld eingegeben und einen Artikel ausgewählt, erscheint der Suchbegriff innerhalb des Artikeltextes fett markiert. </p>
        <h4>Interne Links</h4>
        <p> Wenn in einem Artikel ein anderer Eintrag erwähnt wird, ist der jeweilige Begriff mit einem Link versehen, der zu dem entsprechenden Lexikoneintrag führt. </p>
        <h4>Bildergalerie</h4>
        <p> Am Kopf des Artikels finden Sie eine Bildergalerie. Durch Anklicken gelangen sie zur Illustration und können über die seitlichen Pfeile (vor <img
            src="@/assets/icons/icon_forward.png"
            width="20"
            height="20"
          > und zurück <img
            src="@/assets/icons/icon_backward.png"
            width="20"
            height="20"
          > ) die weiteren Illustrationen des Artikels ansteuern. Durch nochmaliges Anklicken einer Illustration öffnet sich ein neuer Reiter mit der Illustration in der höchsten zur Verfügung gestellten Auflösung sowie ergänzenden Informationen. Zu Bildern von handschriftlichen Quellen finden Sie in diesem Reiter zumeist eine Transkription des Textes. </p>
        <h4>Zitieren eines Lexikonartikels</h4>
        <p> Am Ende eines Artikels finden Sie die vorgeschlagene Zitierweise, die den Permalink zum Text des jeweiligen Lexikonartikels enthält. </p>
        <h4>Ausdrucken eines Lexikonartikels</h4>
        <p> Über das Druckersymbol am Ende eines Artikels öffnet sich der Text des Lexikonartikels in einem neuen Reiter. Sie können den Artikel nun über die Druckfunktion Ihres Browsers ausdrucken oder abspeichern.<br> Über den Button "Artikel mit Karten und Bildern" gelangen Sie zurück zum entsprechenden Artikel im Stadtlexikon.<br> Über den Button "Stadtlexikon" gelangen Sie zur Startseite des Stadtlexikons. </p>
        <h4>Verlinken auf einen Lexikonartikel</h4>
        <p> Über den Button "URL anzeigen" wird in der Browserzeile eines neuen Reiters die vollständige URL angezeigt, die Sie zum Verlinken markieren und kopieren können. Die URL führt direkt auf den Artikel mit Karten und Bildern. Es handelt sich bei dieser URL nicht um einen Permalink, sie sollte daher nicht zum Zitieren verwendet werden. Der Button "URL anzeigen" ist auf Smartphones nicht sichtbar. </p>
        <h4>Publikationszeitpunkt eines Artikels</h4>
        <p> Die Zahl der Lexikonartikel wird kontinuierlich ausgebaut. Jeweils am Ende eines Artikels finden Sie die Angabe, wann der betreffende Artikel im Stadtlexikon publiziert wurde. Sollte dieser Artikel inhaltlich seit der Ersterscheinung aktualisiert worden sein, wird zusätzlich auf die "Ältere Version dieses Artikels" hingewiesen, die über das angezeigte Datum angesteuert werden kann. </p>
        <h4 id="mavos">mit Artikeln verknüpfte Orte (mAvO)</h4>
        <p> Bei zahlreichen Artikeln werden nach dem Öffnen auf der Karte die für diesen Artikel relevanten Orte angezeigt. Das können beispielsweise bei Personen Wohn- und Wirkungsstätten sein, aber auch Stationen eines Ereignisses oder ehemalige Standorte von Institutionen. Die Orte dieses Netzwerkes sind durch einen gelben Punkt markiert. Ein Klick auf die gelbe Markierung öffnet die Informationen zu Bezug und Bedeutung des Ortes. Zu diesen Orten sind keine eigenen Lexikonartikel hinterlegt.<br> Bei einer niedrigen Zoomstufe sind die Orte geclustert, erkennbar an der eingeschriebenen Zahl, die anzeigt, wie viele verschiedene Punkte sich hier verbergen. Wenn Sie die Zoomstufe erhöhen, kommen die hinterlegten Punkte zum Vorschein. Durch Anwählen der Punkte öffnen sich die oben genannten Bezugsinformationen.<br>
          <img src="@/assets/icons/screenshot_bio_net.png">
        </p>
        <hr>
        <h3 id="suche">Suche</h3>
        <h4 id="suchfeld">Suchfeld</h4>
        <p> Geben Sie Ihren Suchbegriff in das Suchfeld ein.
        <ul>
          <li>Groß- und Kleinschreibung spielt keine Rolle.</li>
          <li>Die Suche trunkiert automatisch. Beispielsweise werden bei einer Suche mit dem Suchbegriff "spital" die "Hospitalkirche" und auch der "Spitalhof Möhringen" gefunden.</li>
          <li>Eine Suche nach Jahreszahlen ist möglich.</li>
        </ul>
        </p>
        <p> Nach Eingabe erscheint eine Vorauswahl, in welchen Bereichen des Lexikons der Suchbegriff mit welcher Anzahl von Treffern gefunden wurde. Folgende Bereiche werden dabei ausgegeben:
        <ul>
          <li>Straßennamen – aktuelle und historische</li>
          <li>Artikel</li>
          <li>Illustrationen</li>
          <li>Karten</li>
          <li>Mit Artikeln verknüpfte Orte</li>
        </ul> Sie können den jeweiligen Bereich aufklappen und das gewünschte Ergebnis aus der Liste weiter auswählen. </p>
        <h4>Suchfilter</h4>
        <p> Zusätzlich erlaubt eine Filterfunktion eine weitere Auswahl innerhalb der Artikel nach Personen, Orten, Ereignissen, Themen und Institutionen.<br>
          <img src="@/assets/icons/screenshot_search_filter.png">
        <p> Mit der Schließfunktion [X] können Sie die Filterfunktion zurücksetzen. </p>
        </p>
        <h4>Alle Artikel des Stadtlexikons ansehen</h4>
        <p> Wenn Sie "Alle Artikel" anwählen, werden Ihnen alle Artikel des Lexikons in der Liste, auf der Karte und in der <a href="#zeitleiste">Zeitleiste</a> angezeigt. angezeigt. </p>
        <h4>Straßennamen</h4>
        <p> Wenn Sie aus der Auswahl einen aktuellen Straßennamen anwählen, wird automatisch ein Mapmarker an den Punkt der Karte gesetzt sowie dorthin in die Karte hineingezoomt, wo sich die Straße befindet. Sie können diesen Marker im Fenster "Karteneinstellungen" (über <img
            src="@/assets/icons/icon_map.png"
            width="20"
            height="20"
          > zu öffnen) entfernen.<br> Der Bereich "Straßennamen" umfasst auch "historische Straßennamen", also Straßen, die inzwischen umbenannt worden sind. Durch Anwählen führt auch hier der Mapmarker zu der Straße, zeigt sie auf einer passenden historischen Karte und öffnet zugleich die "Karteneinstellungen".<br> Diese Funktion ist noch nicht für alle Stadtteile verfügbar, der aktuell erfasste Bereich ist aufgelistet. </p>
        <hr>
        <h3 id="karten">Karten</h3>
        <h4>Historische oder thematische Karten auswählen</h4>
        <p> Es gibt mehrere Möglichkeiten, eine Karte auszuwählen.<br> Erscheint die Karte bereits bei der Suche, können Sie diese sofort ansteuern.<br> Möchten Sie aus allen zur Verfügung stehenden Karten auswählen, wählen Sie das Symbol <img
            src="@/assets/icons/icon_map.png"
            width="20"
            height="20"
          >, um das Fenster "Karteneinstellungen" zu öffnen. In zwei Drop-down-Menüs können Sie zwischen "Historischen Karten" und "Thematischen Karten" wählen und eine chronologische Liste der jeweiligen Karten aufrufen. Während die historischen Karten Stadtpläne aus unterschiedlichen Jahren abbilden, sind thematische Karten einer spezifischen Fragestellung (z.B. Sportstätten 1929) unterworfen. Wählen Sie die gewünschte Karte der jeweiligen Rubrik aus.<br> Alle Karten können auch über die Zeitleiste aufgerufen werden. Klicken/tippen Sie auf der Zeitleiste auf das Fähnchen der gewünschten Karte. (Auf Smartphones entfällt die Zeitleiste aufgrund der geringen Bildschirmgröße.)<br> Wenn Sie eine Karte geöffnet haben und nicht mehr wissen, um welche Karte es sich handelt, öffnen Sie das Fenster "Karteneinstellungen". Die ausgewählte Karte ist dort fett markiert. Zusätzliche Informationen zu der ausgewählten Karte sind im Fenster "Karteneinstellungen" über das Informationssymbol <img
            src="@/assets/icons/icon_map_info.png"
            width="20"
            height="20"
          > abrufbar. </p>
        <h4>Kartenwarnung</h4>
        <p> Sollte die ausgewählte Karte nicht im sichtbaren Ausschnitt Ihres Bildschirms liegen, erscheint automatisch ein Hinweis mit der Option, den Bildschirmausschnitt entsprechend anzupassen. </p>
        <h4 id="kartenzoom">Kartenausschnitt und Kartengröße verändern</h4>
        <p> Sie können die Karte verschieben, indem Sie die linke Maustaste gedrückt halten und die Maus bewegen, oder indem Sie bei entsprechenden Geräten mit dem Finger die Karte verschieben. </p>
        <p> Sie können in eine Karte hineinzoomen, indem Sie auf das <img
            src="@/assets/icons/icon_map_plus.png"
            width="20"
            height="20"
          >-Symbol am Rand klicken oder – falls vorhanden – das Mausrad zur Steuerung benutzen; bei einem Tablet oder Smartphone können Sie die Karte mittels Daumen und Zeigefinger auseinanderziehen. Das Zoomen mit Daumen und Zeigefinger funktioniert nicht durch Marker, Vorschaubilder oder die Zeitleiste hindurch. </p>
        <p> Das Herauszoomen aus der Karte funktioniert entsprechend über das <img
            src="@/assets/icons/icon_map_minus.png"
            width="20"
            height="20"
          >-Symbol oder durch das Zusammenziehen von Daumen und Zeigefinger. </p>
        <h4>Transparenz der Karten verändern</h4>
        <p> Im Fenster "Karteneinstellungen" (über <img
            src="@/assets/icons/icon_map.png"
            width="20"
            height="20"
          > zu öffnen) erscheint ein Transparenz-Regler, sobald eine historische Karte ausgewählt wurde:<br>
          <img src="@/assets/icons/screenshot_transparency.png">
          <br> Voreingestellt ist eine Transparenz von 50%. Das bedeutet, dass Sie die heutige Stadtkarte unter der historischen Karte sehen können.
        </p>
        <p> Sie können die Transparenz der historischen Karte mit der Maus oder mit dem Finger zwischen 0% (die historische Karte verdeckt die heutige Stadtkarte ganz) und 100% (die historische Karte ist ganz transparent und nicht mehr zu sehen) verändern. </p>
        <h4 id="ausrichten">Historische und thematische Karten ausrichten und wieder norden</h4>
        <p> Alle Karten werden im Stadtlexikon automatisch genordet. Das führt dazu, dass manche Karten unter Umständen schief auf der heutigen Stadtkarte liegen.<br>
          <img
            src="@/assets/icons/screenshot_map.png"
            style="width: 50%; height: 50%;"
          >
        </p>
        <p> Sie können diese Karten im Fenster "Karteneinstellungen" (über <img
            src="@/assets/icons/icon_map.png"
            width="20"
            height="20"
          > zu öffnen) ausrichten, indem Sie auf die Schaltfläche "Karte ausrichten" gehen. Alternativ können Sie die Karte auf einem Tablet oder Smartphone auch mit den Fingern drehen, am PC mit der Shift-Taste und der Maus. </p>
        <p> Sobald Sie eine Karte ausrichten, erscheint links ein Pfeil <img
            src="@/assets/icons/icon_north.png"
            width="20"
            height="20"
          >. Klicken/tippen Sie auf diesen Pfeil, so wird die Karte wieder genordet. Gleiches geschieht, wenn Sie ein zweites Mal auf die Schaltfläche "Historische Karte ausrichten" klicken/tippen. </p>
        <h4>Historische Karten schließen</h4>
        <p> Im Fenster "Karteneinstellungen" (über <img
            src="@/assets/icons/icon_map.png"
            width="20"
            height="20"
          > zu öffnen) können Sie über die Schaltfläche "Keine Karte" eine geöffnete historische Karte schließen. </p>
        <p> Wenn Sie eine andere Karte auswählen, wird die zuerst ausgewählte Karte automatisch geschlossen. (Ausnahme: Sie öffnen die zweite Karte über die Funktion "Karte vergleichen".) </p>
        <h4>Historische Karten vergleichen</h4>
        <p> Sie können zwei Karten miteinander vergleichen. Wählen Sie dazu im Fenster "Karteneinstellungen" (über <img
            src="@/assets/icons/icon_map.png"
            width="20"
            height="20"
          > zu öffnen) eine erste Karte aus. Die Funktion "Karte vergleichen mit …" ist dann aktiviert. Klicken/tippen Sie auf diese Schaltfläche und wählen Sie aus der nun erscheinenden Liste eine zweite Karte. Über den Transparenz-Regler können Sie die Transparenz dieser zweiten Karte verändern und die beiden ausgewählten Karten miteinander vergleichen.<br> Diese Funktion steht auf Smartphones nicht zur Verfügung. </p>
        <h4>Ortungsfunktion</h4>
        <p> Durch das Antippen des Ortungssymbols am linken Rand können Sie sich auf der Karte anzeigen lassen, wo Sie sich gerade befinden. Ihr Standort wird durch einen blauen Punkt angezeigt.<br> Hierzu müssen Sie die die Ortungsfunktion in den Einstellungen Ihres mobilen Endgerätes aktiviert und Ihrem Browser erlaubt haben, darauf zuzugreifen.<br> Diese Funktion entfällt in der Desktop-Version. </p>
        <h4>Ein- und ausblenden der Kartenmarker</h4>
        <p> Im Fenster "Karteneinstellungen" (über <img
            src="@/assets/icons/icon_map.png"
            width="20"
            height="20"
          > zu öffnen) finden Sie zwei Schaltflächen "aus-/einblenden", mit denen Sie einerseits die Artikelmarker (<img
            src="@/assets/icons/icon_mapmarker.png"
            width="20"
            height="20"
          >) und die Straßenmarker (<img
            src="@/assets/icons/icon_streetmarker.png"
            width="17"
            height="24"
          >) ein- und ausblenden können, wenn diese Sie beim Betrachten der Karte stören. </p>
        <hr>
        <h3 id="zeitleiste">Zeitleiste</h3>
        <p> Alle Artikel des Stadtlexikons und alle historischen Karten sind auf der Zeitleiste verortet und über die Zeitleiste ansteuerbar. Auf Smartphones entfällt die Zeitleiste aufgrund der geringen Bildschirmgröße.<br> Artikel erscheinen auf der Zeitleiste, wenn sie in der Vorauswahl Teil der Ergebnismenge sind. Ist kein Artikel ausgewählt, sind ausschließlich die historischen Karten verortet. Sind "Alle Artikel" ausgewählt, kann es in Zeitabschnitten zu unübersichtlichen Überblendungen kommen. Nutzen Sie das Lupensymbol am linken Rand, um den Bereich anzupassen.<br> Werden mit Artikeln verknüpfte Orte angezeigt, wird die Zeitleiste ausgeblendet. Durch Schließen des Artikels wird sie wieder eingeblendet. </p>
        <h4>Navigieren auf der Zeitleiste</h4>
        <p> Sie können die Zeitleiste verschieben, indem Sie die linke Maustaste gedrückt halten und die Maus bewegen, oder indem Sie bei entsprechenden Geräten mit dem Finger die Zeitleiste verschieben. </p>
        <p> Um den auf der Zeitleiste sichtbaren Bereich zu verkleinern oder zu vergrößern, verwenden Sie das Lupensymbol mit + bzw. - am linken Rand der Zeitleiste: <br>
          <img src="@/assets/icons/icon_timeslider_mag.png">
        </p>
        <hr>
        <h2 id="faq">FAQs</h2>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i>Wie komme ich wieder zur Startseite?</em>
        <p> Gehen Sie in der Adresszeile des Browsers auf das Symbol <img src="@/assets/icons/icon_reload.png"> (Reload). </p>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i> Auf dem Tablet oder Smartphone kann ich nicht durch das Auseinanderziehen zweier Finger die Karte vergrößern.</em>
        <p> Diese Gestensteuerung funktioniert nicht durch geöffnete Vorschaubilder oder durch die Zeitleiste hindurch. Vergrößern Sie die Karte mit Hilfe des <img
            src="@/assets/icons/icon_map_plus.png"
            width="20"
            height="20"
          > am linken Rand. </p>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i> Die Karten laden sehr langsam.</em>
        <p> Um die Karten in der gewünschten Qualität abbilden zu können und einen tiefen Zoom zu ermöglichen, sind die Dateien sehr groß. Insbesondere wenn Sie mit einem älteren Gerät mit wenig Arbeitsspeicher arbeiten oder wenn Ihre Verbindung zum Internet langsam ist, kann das Laden etwas länger dauern. </p>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i>Ich sehe die ausgewählte Karte nicht und es kam kein Hinweis, dass sie außerhalb des sichtbaren Bereichs liegt.</em>
        <p> Wenn nur ein kleiner Teil im Bildschirmausschnitt sichtbar ist, kann die Karte leicht übersehen werden. Verringern Sie die Zoomstufe – und die Karte sollte sichtbar werden. Sollte die Karte immer noch nicht erkennbar sein, überprüfen Sie in den Karteneinstellungen, welcher Prozentwert bei der Transparenz eingestellt ist. </p>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i> Ich sehe keine Zeitleiste.</em>
        <p> Aufgrund der geringen Bildschirmgröße von Smartphones entfällt die Zeitleiste dort. Wenn Sie über einen PC oder ein Tablet auf das Stadtlexikon zugreifen und keine Zeitleiste sehen, verwenden Sie möglicherweise einen Browser, der vom Stadtlexikon nicht unterstützt wird, oder eine veraltete Version eines unterstützten Browsers. </p>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i> Welche Browser unterstützt das Stadtlexikon?</em>
        <p> Das Stadtlexikon unterstützt Firefox, Safari, Chrome, Opera und Edge in den aktuellen Versionen. Veraltete Versionen können zu Darstellungsproblemen führen. </p>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i> Ich habe eine Karte geöffnet. Wo finde ich die Information, aus welchem Jahr die Karte stammt?</em>
        <p> Öffnen Sie über <img
            src="@/assets/icons/icon_map.png"
            width="20"
            height="20"
          > das Fenster "Karteneinstellungen". Die ausgewählte Karte ist dort fett markiert. Über <img
            src="@/assets/icons/icon_map_info.png"
            width="20"
            height="20"
          > erhalten Sie zusätzliche Informationen zur jeweiligen Karte. </p>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i> Warum kann ich die Illustrationen und Quellenbeispiele herunterladen, nicht aber die Karten?</em>
        <p> Aus technischen Gründen ist das Herunterladen der Karten nicht möglich. </p>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i> Ich habe eine Illustration heruntergeladen, doch genügt mir die Auflösung nicht. Was kann ich tun?</em>
        <p> Abbildungen bieten wir aus technischen Gründen in der gegebenen Größe an. Sollten Sie eine höhere Auflösung benötigen, wenden Sie sich bitte direkt an uns. In jedem Fall müssen Sie bei einer Weiterverwendung die Bildrechte beachten. </p>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i> Ich möchte eine von mir ausgewählte Karte schließen, sie hat aber kein schwarzes Kreuzchen in der oberen Ecke, mit dem ich sie schließen könnte.</em>
        <p> Die zur Auswahl stehenden Karten lassen sich nur über das Fenster "Karteneinstellungen" (über <img
            src="@/assets/icons/icon_map.png"
            width="20"
            height="20"
          > zu öffnen) schließen. Es handelt sich technisch bei diesen Karten nicht um Fenster, sondern um Layer, daher ist es nicht möglich, die von Fenstern bekannte Schließfunktion über Kreuzchen anzubieten. </p>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i> Warum sehe ich auf der <a href="#zeitleiste">Zeitleiste</a> mal mehr, mal weniger Artikelfähnchen?</em>
        <p> Die Auswahl der Artikelfähnchen auf der Zeitleiste reagiert auf Ihre Eingaben. Wenn Sie einen Filter gesetzt oder eine Suche durchgeführt haben, sehen Sie auf der Zeitleiste nur die Fähnchen der Artikel, die Teil der Ergebnismenge sind. </p>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i> Warum haben die Kreise (Marker) manchmal unterschiedliche Farben (Rot, Braun oder Gelb)?</em>
        <p> Die braunen Marker zeigen an, an welchen Stellen Lexikonartikel verortet sind. Sie wechseln ihre Farbe auf Rot, wenn Sie einen Filter gesetzt haben oder eine Suche durchgeführt haben. Alle Marker, die auf einen Artikel verweisen, der Teil der Ergebnismenge ist, werden rot. Die übrigen Marker bleiben braun.<br>
          <a href="#mavos">Gelbe Marker</a> erscheinen auf der Karte, wenn Sie einen Artikel geöffnet haben, mit dem Orte verknüpft sind (mAvOs).
        </p>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i> Auf manchen Kreisen stehen Zahlen, auf anderen nicht. Was bedeutet das?</em>
        <p> Wenn ein Kreis eine Zahl enthält, sind an dieser Stelle mehrere Einzelpunkte (mAvOs oder Lexikonartikel) verortet. Wenn Sie tiefer in die Karte <a href="#kartenzoom">hineinzoomen</a>, trennen sich die einzelnen Marker voneinander. Sobald keine Zahl mehr eingetragen ist, können sie angewählt werden. </p>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i> Auf der linken Seite erscheint plötzlich ein Pfeil-Symbol.</em>
        <p> Wenn links <img
            src="@/assets/icons/icon_north.png"
            width="20"
            height="20"
          > erscheint, ist die <a href="#ausrichten"> historische oder thematische Karte</a> nicht mehr genordet. Auf einem mobilen Endgerät kann es passieren, dass Sie beim Navigieren über die Gestensteuerung die Karte aus Versehen drehen. Der Pfeil weist Sie darauf hin. Wenn Sie auf den Pfeil klicken/tippen, wird die Karte wieder genordet. </p>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i> Ich möchte den Begrüßungstext nochmals lesen. Wie kann ich ihn aufrufen?</em>
        <p> Am rechten Rand ist eine Leiste mit Pfeil angezeigt. Dort können Sie die sogenannte Bauchbinde nochmals öffnen. </p>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i> Auf der Zeitleiste sind so viele Artikel angezeigt, dass ich sie nicht unterscheiden kann.</em>
        <p> Nutzen Sie die Lupenfunktion am linken Rand, um den Ausschnitt zu vergrößern. Durch ein sogenanntes Mouseover wird sichtbar, welcher Artikel ausgewählt werden kann. </p>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i> Auf dem Smartphone sind die Punkte verschoben, obwohl es vorher lief.</em>
        <p> Möglicherweise müssen Sie den Cache (Zwischenspeicher) Ihres Gerätes leeren. Hinweise zur Durchführung stellt Ihr Anbieter (Safari, Android) auf den entsprechenden Hilfeseiten zur Verfügung. </p>
        <em><i
            class="fa fa-arrow-right"
            aria-hidden="true"
          ></i> Wohin kann ich mich wenden, wenn ich Fragen oder Anregungen habe?</em>
        <p> Über euer Feedback freuen wir uns sehr. Schreibe uns am besten eine E-Mail an: <a href="mailto:tonka@spicetech.de">tonka@spicetech.de</a>
        </p>
        <div class="impressum">
          <div style="position: absolute; right: 95px; bottom: 10px; z-index: 1000000;">
            <a
              href="[Link zur Datenschutzerklärung]"
              class="dts-impressum"
            >Datenschutzerklärung</a>
          </div>
          <div style="position: absolute; right: 25px; bottom: 10px; z-index: 1000000;">
            <a
              href="[Link zum Impressum]"
              class="dts-impressum"
            >Impressum</a>
          </div>
        </div>
      </v-card-text>
    </v-card>
  </v-container>
</template>
<script setup>
import { onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useA11y } from '../composables/a11y';

const route = useRoute();
const queryParams = route.query;
const { switchTheme } = useA11y();

onMounted(() => {
  if (queryParams.a11yTheme === 'true') {
    switchTheme();
  }
});
</script>
<style lang="scss">
.v-card .v-card-text {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 25px;
}

h2 {
  font-size: 30px;
}

h3 {
  font-size: 24px;
}

h4 {
  font-size: 18px;
}

p {
  margin: 0 0 10px;
}

a {
  color: #337ab7;
  text-decoration: none;
}

ul {
  padding-left: 25px;
}

img {
  max-width: 100%;
}

.v-card-item__append {
  text-align: end;
}

@media only screen and (max-width: 480px) {
  .v-card-item {
    display: flex;
  }

  h2 {
    line-height: 1;
  }
}

@media (min-width: 1024px) {
  .about-help {
    width: 70%;
  }
}
</style>
