import { defineConfig } from 'vitepress'
import { tabsMarkdownPlugin } from 'vitepress-plugin-tabs'
import { enConfig } from './locales/en'
import { uaConfig } from './locales/ua'

export default defineConfig({
  lastUpdated: true,
  scrollOffset: 'header',
  cleanUrls: true,

  title: "Inventory Weight",
  description: "documentation for the mod that adds weight to items.",

  head: [
    ['link', { rel: 'icon', href: 'https://i.imgur.com/kBhgr4Z.png' }],
    ['meta', { name: 'viewport', content: 'width=device-width,initial-scale=1' }],
  ],

  markdown: {
    config(md) {
      md.use(tabsMarkdownPlugin)
    }
  },

  base: '/inventory-weight/',

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
