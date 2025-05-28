export function getIconByLemmaType(lemmaType) {
    switch (lemmaType) {
        case 'PLACE':
            return 'fa fa-flag';
        case 'PERSON':
            return 'fa fa-user';
        case 'EVENT':
            return 'fa fa-calendar';
        case 'INSTITUTION':
            return 'fa fa-university';
        case 'TOPIC':
            return 'fa fa-book'
        case "BUILDING":
            return 'fa fa-building';
        case "DEVICE":
            return 'fa fa-laptop';
        case "OTHER":
            return 'fa fa-image';
        default:
            return '';
    }
};
