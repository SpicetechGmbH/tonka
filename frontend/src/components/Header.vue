<template>
  <div class="mainHeader hidden-sm-and-down d-md-flex rounded-lg ml-4 pa-2 cursor-pointer">
    <div class="subHeader">
      <div
        class="link"
        @click="routeToAboutPage"
      >
        <span class="font-istok-web eight-font-size">STADT</span>
        <span class="font-lusitana eight-font-size">ARCHIV&nbsp;</span>
        <span class="font-istok-web eight-font-size">STUTTGART</span>
        <br />
        <p>Stadtlexikon</p>
      </div>
    </div>
    <div class="image d-sm-flex d-none">
      <a
        href="https://www.stuttgart.de/"
        target="_blank"
      >
        <img
          width="70"
          height="50"
          src="@/assets/logo_short.png"
          alt="Stuttgart"
        />
      </a>
    </div>
  </div>
</template>

<script setup>
import { useTheme } from 'vuetify';

const theme = useTheme();

function routeToAboutPage() {
  window.open(`/wirueberuns?theme=${ theme.global.name.value === 'a11yDtsTheme' ? 'a11y' : 'default' }`, '_blank');

}
</script>

<style scoped lang="scss">
.mainHeader {
  background-color: white;
  opacity: 80%;
  width: max-content;
  border: solid 0.1em rgba(#000, 0.2);

  & .subHeader {
    padding-left: 35px;
    padding-right: 30px;

    & span {
      color: black;
    }

    & p {
      font-size: 18pt;
      font-weight: bold;
      font-family: arial, serif;
    }

    & .link {
      text-decoration: none;
      color: black;
    }
  }

  & .image {
    padding-right: 10px;

    & img {
      padding-left: 10px;
      max-width: 100%;
      height: auto;
    }
  }
}

@media only screen and (max-width: 780px) {
  .mainHeader {
    & .subHeader {
      padding-left: 5px;
      padding-right: 0px;
    }
  }
}
</style>
