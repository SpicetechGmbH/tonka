<template>
    <div
        v-show="title != null || description != null"
        class="notification"
        :class="{ 'expand': expand }"
    >
        <div class="notification__title">
            <v-icon
                class="icon"
                @click="notificationExpand"
            > {{ expand ? 'mdi:mdi-menu-left' : 'mdi:mdi-menu-right' }} </v-icon>
        </div>
        <div class="notification__content">
            <h3>{{ title }}</h3>
            <p v-html="description"></p>
        </div>
    </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import services from '@/services';

const expand = ref(true);
const title = ref(null);
const description = ref(null);

function notificationExpand() {
    expand.value = !expand.value;
};

onMounted(() => {
    services.startMessage.get().then((response) => {
        title.value = response.data.title
        description.value = response.data.description
    });
    setTimeout(() => {
        expand.value = false;
    }, 4000);
});
</script>
<style lang="scss">
.notification {
    background: rgb(255 255 255 / 80%);
    padding: 1rem;
    padding-top: 1.5rem;
    position: fixed;
    right: 0;
    top: 30%;
    width: 1rem;
    overflow: hidden;
    height: 30%;
    border-top-left-radius: 4px;
    border-bottom-left-radius: 4px;
    transition: width 0.8s ease-in-out;

    &.expand {
        width: 80%;
        height: 30%;
        z-index: 10;
        overflow-y: auto;
    }

    &__title {
        display: flex;
        flex-direction: row-reverse;
        justify-content: space-between;
        align-items: center;
        transform: rotate(180deg);
        cursor: pointer;
    }

    &__content {
        writing-mode: horizontal-tb;
        padding: 0 3rem;

        h3 {
            font-size: 24px;
            font-weight: 600;
            line-height: 1.1;
            padding-bottom: 10px;
        }

        p {
            font-size: 14px;
        }
    }
}

.icon {
    font-size: 3rem;
    color: #000000a2;
    margin: -25px;
}
</style>
