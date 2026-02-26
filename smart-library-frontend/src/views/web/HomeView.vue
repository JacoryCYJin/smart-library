<template>
  <div class="flex flex-col">
    <PhysicsHero />

    <HomeStats />

    <HomeBentoGrid />
  </div>
</template>

<script setup>
import PhysicsHero from '@/components/home/PhysicsHero.vue'
import HomeStats from '@/components/home/HomeStats.vue'
import HomeBentoGrid from '@/components/home/HomeBentoGrid.vue'
</script>
