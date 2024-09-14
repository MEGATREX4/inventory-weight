import type { DefaultTheme, LocaleSpecificConfig } from 'vitepress'

export const uaConfig: LocaleSpecificConfig<DefaultTheme.Config> = {
  themeConfig: {
    nav: [
      { text: 'Головна', link: '/ua/' }
    ],

    search: {
      options: {
        translations: {
          button: {
            buttonText: 'Пошук',
            buttonAriaLabel: 'Пошук',
          },
          modal: {
            displayDetails: 'Відобразити детальний список',
            noResultsText: 'Нічого не знайшли',
            resetButtonTitle: 'Скинути пошук',
            footer: {
              selectText: 'для вибору',
              selectKeyAriaLabel: 'enter',
              navigateText: 'для навігації',
              navigateUpKeyAriaLabel: 'стрілка вгору',
              navigateDownKeyAriaLabel: 'стрілка вниз',
              closeText: 'закрити',
              closeKeyAriaLabel: 'escape',
            },
          },
        },
      },
    },

    sidebar: [
      {
        text: 'Документація',
        items: [
          { text: 'Головна', link: '/ua/' },
          { text: 'Вага Інвентарю', link: '/ua/inventory-weight' },
          {
            text: 'Керівництво',
            items: [
              { text: 'Вступ', link: '/ua/guide/introduction' }
            ],
          },
        ],
      },
    ],

    outline: {
      label: 'У цьому параграфі',
      level: 'deep',
    },

    docFooter: {
      prev: 'Попередня сторінка',
      next: 'Наступна сторінка',
    },

    lastUpdated: {
      text: 'Оновлено в',
      formatOptions: {
        dateStyle: 'full',
        timeStyle: 'medium',
      },
    },

    editLink: {
      pattern: 'https://github.com/MEGATREX4/inventory-weight/edit/docs/inventory-weight/:path',
      text: 'Suggest changes to this page',
    },
  },
}
