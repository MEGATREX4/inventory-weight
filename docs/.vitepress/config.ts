import { defineConfig } from 'vitepress'
import { tabsMarkdownPlugin } from 'vitepress-plugin-tabs'
import { enConfig } from './locales/en'
import { uaConfig } from './locales/ua'

export default defineConfig({
  lastUpdated: true,
  scrollOffset: 'header',
  cleanUrls: true,

  title: "Inventory Weight",
  description: "A site about whatever you want.",

  head: [
    ['link', { rel: 'icon', href: '/logo.ico' }],
    ['meta', { name: 'viewport', content: 'width=device-width,initial-scale=1' }],
  ],

  markdown: {
    config(md) {
      md.use(tabsMarkdownPlugin)
    }
  },

  base: '/docs/',

  locales: {
    root: {
      label: 'English',
      lang: 'en',
      link: '/',
      ...enConfig,
    },
    ua: {
      label: 'Українська',
      lang: 'ua',
      link: '/ua/',
      ...uaConfig,
    }
  }
})
