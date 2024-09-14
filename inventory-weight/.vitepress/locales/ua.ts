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
        items: [
          { text: 'Головна', link: '/ua/' },
          {
            text: 'Як почати',
            items: [
              { text: 'Встановлення', link: '/ua/guide/installation' },
              { text: 'Сумісність', link: '/ua/guide/compatibility' },
              { text: 'Поширені питання', link: '/ua/guide/faq' }
            ]
          },
          {
            text: 'Опції',
            items: [
              { text: 'Датапаки', link: '/ua/guide/datapacks' },
              { text: 'Налаштування сервера', link: '/ua/guide/options/inventory_weights_server' },
              { text: 'Налаштування клієнта', link: '/ua/guide/options/inventory_weights_client' },
              { text: 'Налаштування ваги предметів', link: '/ua/guide/options/inventory_weights_items' },
            ]
          },
          {
            text: 'Функції',
            items: [
              { text: 'Стандартні значення предметів', link: '/ua/guide/features/item_default_values' },
              { text: 'Користувацькі значення предметів', link: '/ua/guide/features/item_custom_values' },
              { text: 'Кишені на броні', link: '/ua/guide/features/pockets' },
              { text: 'Вага інвентаря', link: '/ua/guide/features/max_weight' },
              { text: 'Перевантаження', link: '/ua/guide/features/overload_effect' },
              { text: 'Команди', link: '/ua/guide/features/commands' },
              { text: 'Підказки', link: '/ua/guide/features/tooltips' },
              { text: 'HUD', link: '/ua/guide/features/hud' },
            ]
          }
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
