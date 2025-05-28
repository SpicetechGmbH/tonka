<template>
  <div class="mainHeader">
    <div class="subHeader">
      <div
        class="link"
        @click="routeToAboutPage"
      >
        <span class="font-istok-web eight-font-size">Lexikon</span>
        <br />
        <p>Spicetech</p>
      </div>
    </div>
    <div class="image d-sm-flex d-none">
      <a
        href="https://www.spicetech.de/"
        target="_blank"
      >
        <img
          width="70"
          height="50"
          src="@/assets/logo_short.png"
          alt="Logo"
        />
      </a>
    </div>
  </div>
</template>
  
<script setup>
import { useTheme } from 'vuetify';

const theme = useTheme();

function routeToAboutPage() {
  window.open(`/wirueberuns?a11yTheme=${ theme.global.name.value === 'a11yDtsTheme' }`, '_blank');

}
</script>
  
<style lang="scss">
$b: 0.1em;

.mainHeader {
  cursor: pointer;
  display: flex;
  background-color: white;
  padding: 8px;
  border-radius: 7px;
  opacity: 80%;
  width: max-content;
  border: solid $b rgba(#000, 0.2);
  margin-left: 15px;

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

    & .image {
      & img {
        display: none;
      }
    }
  }
}
</style>
