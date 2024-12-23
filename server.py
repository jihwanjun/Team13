from flask import Flask, request, jsonify
from bs4 import BeautifulSoup
import urllib.request

hdr = {
    'User-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 Edg/131.0.0.0'}

app = Flask(__name__)


def crawl(url):
    soup = BeautifulSoup(
        urllib.request.urlopen(
            urllib.request.Request(url, headers=hdr)
        )
        .read().decode('utf-8', 'ignore'),
        'html.parser'
    )
    for br in soup.find_all("br"):
        br.replace_with("\n")
    return soup


# true/false
QUERY_MAP = {
    'pageIndex': 'pageIndex',

    'type': 'sc_plcyFldCd',  # 유형
    'job': '023010',  # 일자리
    'residence': '023020',  # 주거
    'education': '023030',  # 교육
    'welfareCulture': '023040',  # 복지.문화
    'right': '023050',  # 참여.권리

    'employmentTarget': 'sc_trgtNm',  # 취업상태
    'employed': 'ESC_02',  # 재직자
    'selfEmployed': 'ESC_03',  # 자영업자
    'unemployed': 'ESC_04',  # 미취업자
    'freelancer': 'ESC_05',  # 프리랜서
    'dailyWorker': 'ESC_06',  # 일용근로자
    'startup': 'ESC_07',  # (예비)창업자
    'shortTerm': 'ESC_08',  # 단기근로자
    'farmer': 'ESC_09',  # 영농종사자
    'noMatter': 'ESC_01',  # 제한없음

    'recruitmentStatus': 'sc_rcritCurentSitu',  # 모집현황
    'regular': '001',  # 상시
    'underWay': '002',  # 모집중
    'toBe': '003',  # 모집예정
    'end': '004',  # 마감

    'ageGroup': 'sc_age',  # 연령대
    '19~24': '001',  # 19~24
    '25~29': '002',  # 25~29
    '30~34': '003',  # 30~34
    '35~39': '004',  # 35~39

    'agency': 'sc_plcyBizRgnSe',  # 담당기관
    'seoulSi': '001',  # 서울시
    'seoulGu': '002',  # 자치구

    'seoulRegion': 'sc_plcyBizInstCd',  # 자치구
    'jongro': '003002001001',  # 종로구
    'jung': '003002001002',  # 중구
    'yongsan': '003002001003',  # 용산구
    'seongdong': '003002001004',  # 성동구
    'gwangjin': '003002001005',  # 광진구
    'dongdaemun': '003002001006',  # 동대문구
    'jungnang': '003002001007',  # 중랑구
    'seongbuk': '003002001008',  # 성북구
    'gangbuk': '003002001009',  # 강북구
    'dobong': '003002001010',  # 도봉구
    'nowon': '003002001011',  # 노원구
    'eunpyeong': '003002001012',  # 은평구
    'seodaemun': '003002001013',  # 서대문구
    'mapo': '003002001014',  # 마포구
    'yangcheon': '003002001015',  # 양천구
    'gangseo': '003002001016',  # 강서구
    'guro': '003002001017',  # 구로구
    'geumcheon': '003002001018',  # 금천구
    'yeongdeungpo': '003002001019',  # 영등포구
    'dongjak': '003002001020',  # 동작구
    'gwanak': '003002001021',  # 관악구
    'seocho': '003002001022',  # 서초구
    'gangnam': '003002001023',  # 강남구
    'songpa': '003002001024',  # 송파구
    'gangdong': '003002001025',  # 강동구

    'region': 'sc_plcyBizInstCd',  # 지역구분
    'busan': '003002002',  # 부산
    'daegu': '003002003',  # 대구
    'incheon': '003002004',  # 인천
    'gwangju': '003002005',  # 광주
    'daejeon': '003002006',  # 대전
    'ulsan': '003002007',  # 울산
    'gyeonggi': '003002008',  # 경기
    'gangwon': '003002009',  # 강원
    'northChungcheong': '003002010',  # 충북
    'southChungcheong': '003002011',  # 충남
    'northJeolla': '003002012',  # 전북
    'southJeolla': '003002013',  # 전남
    'northGyeongsang': '003002014',  # 경북
    'southGyeongsang': '003002015',  # 경남
    'jeju': '003002016',  # 제주
    'sejong': '003002017',  # 세종

    'central': 'central',  # 중앙정부
}
TYPE: "type"  # 유형
EMPLOYMENT_TARGET= 'employmentTarget'  # 취업상태
RECRUITMENT_STATUS= 'recruitmentStatus'  # 모집현황
AGE_GROUP= 'ageGroup'  # 연령대
AGENCY= 'agency'  # 담당기관
SEOUL_REGION= 'seoulRegion'  # 자치구
REGION= 'region'  # 지역구분
CENTRAL= 'central'  # 중앙정부

SEOUL_SI='seoulSi'
SEOUL_GU='seoulGu'


@app.post('/search')
def search():
    data = request.get_json()
    query_list = []
    region = ('plcyInfo', 'ctList', 2309150002, '002')
    for (key, value) in data.items():
        if key not in QUERY_MAP or value is None:
            continue
        if key == CENTRAL:
            region = ('youthPlcyInfo', 'list1', 2309160001, '002')
        else:
            if key == AGENCY:
                if value == SEOUL_SI:
                    region = ('plcyInfo', 'ctList', 2309150002, '002')
                elif value == SEOUL_GU:
                    region = ('plcyInfo', 'guList', 2309150002, '003')
            elif key == REGION and region[1] != 'guList':
                region = ('youthPlcyInfo', 'list2', 2309160001, '003')
            if isinstance(value, list):
                for item in value:
                    query_list.append(f'{QUERY_MAP[key]}={QUERY_MAP[item] if item in QUERY_MAP else item}')
            else:
                query_list.append(f'{QUERY_MAP[key]}={QUERY_MAP[value] if value in QUERY_MAP else value}')
    query_raw = '&'.join(query_list)
    print(f'https://youth.seoul.go.kr/infoData/{region[0]}/{region[1]}.do?&key={region[2]}&orderBy=regYmd+desc&tabKind={region[3]}&{query_raw}')
    soup = crawl(
        f'https://youth.seoul.go.kr/infoData/{region[0]}/{region[1]}.do?&key={region[2]}&orderBy=regYmd+desc&tabKind={region[3]}&{query_raw}')
    ul = soup.find('ul', class_='policy-list').find_all('li')

    result = []
    try:
        for li in ul:
            dict = {}
            dict['title'] = li.a.text
            dict['pagekey'] = li.a['onclick'].split('\'')[1]
            dict['desc'] = li.em.text
            result.append(dict)
    except Exception as e:
        print(e)
    return jsonify(result)


def parse_table(table):
    result = []
    rows = table.tbody.find_all('tr')
    for row in rows:
        contents = []
        ths = row.find_all('th')
        tds = row.find_all('td')
        for i in range(len(ths)):
            contents.append({
                "title": ths[i].text.strip(),
                "content": tds[i].text.strip()
            })
        result.append(contents)
    return result


@app.get('/page/<page_key>')
def page(page_key):
    soup = crawl(f'https://youth.seoul.go.kr/infoData/plcyInfo/view.do?plcyBizId={page_key}')
    policy_detail = soup.find('div', class_='policy-detail')

    result = {}
    try:
        strongs = policy_detail.find_all('strong')
        tables = policy_detail.find_all('table')
        result['title'] = strongs[0].text
        result['tables'] = [
            {
                'title': strongs[1].text,
                'table': parse_table(tables[0])
            },
            {
                'title': strongs[2].text,
                'table': parse_table(tables[1])
            },
            {
                'title': strongs[3].text,
                'table': parse_table(tables[2])
            },
            {
                'title': strongs[4].text,
                'table': parse_table(tables[3])
            }
        ]
    except Exception as e:
        print(e)
    return jsonify(result)

app.run(host='0.0.0.0', port=80)
