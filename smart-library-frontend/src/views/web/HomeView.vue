<template>
  <div class="flex flex-col">
    <PhysicsHero />

    <BookHistoryStory />

    <!-- 暂时注释掉统计数据和 Bento 网格 -->
    <!-- <HomeStats /> -->
    <!-- <HomeBentoGrid /> -->
  </div>
</template>

<script setup>
import PhysicsHero from '@/components/home/PhysicsHero.vue'
import BookHistoryStory from '@/components/home/BookHistoryStory.vue'
// import HomeStats from '@/components/home/HomeStats.vue'
// import HomeBentoGrid from '@/components/home/HomeBentoGrid.vue'
</script>
