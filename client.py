import requests

PageIndex = 'pageIndex'  # 1 당 5개 검색됨

TYPE = 'sc_plcyFldCd'  # 유형
JOB = '023010'  # 일자리
RESIDENCE = '023020'  # 주거
EDUCATION = '023030'  # 교육
WELFARE_CULTURE = '023040'  # 복지.문화
RIGHT = '023050'  # 참여.권리

EMPLOYMENT_TARGET = 'sc_trgtNm'  # 취업상태
EMPLOYED = 'ESC_02'  # 재직자
SELF_EMPLOYED = 'ESC_03'  # 자영업자
UNEMPLOYED = 'ESC_04'  # 미취업자
FREELANCER = 'ESC_05'  # 프리랜서
DAILY_WORKER = 'ESC_06'  # 일용근로자
STARTUP = 'ESC_07'  # (예비)창업자
SHORT_TERM = 'ESC_08'  # 단기근로자
FARMER = 'ESC_09'  # 영농종사자
NO_MATTER = 'ESC_01'  # 제한없음

RECRUITMENT_STATUS = 'sc_rcritCurentSitu'  # 모집현황
REGULAR = '001'  # 상시
UNDER_WAY = '002'  # 모집중
TO_BE = '003'  # 모집예정
END = '004'  # 마감

AGE_GROUP = 'sc_age'  # 연령대
_19_24 = '001'  # 19~24
_25_29 = '002'  # 25~29
_30_34 = '003'  # 30~34
_35_39 = '004'  # 35~39

AGENCY = 'sc_plcyBizRgnSe'  # 담당기관
SEOUL_SI = '001'  # 서울시
SEOUL_GU = '002'  # 자치구

SEOUL_REGION = 'sc_plcyBizInstCd'  # 자치구
JONGRO = '003002001001'  # 종로구
JUNG = '003002001002'  # 중구
YONGSAN = '003002001003'  # 용산구
SEONGDONG = '003002001004'  # 성동구
GWANGJIN = '003002001005'  # 광진구
DONGDAEMUN = '003002001006'  # 동대문구
JUNGNANG = '003002001007'  # 중랑구
SEONGBUK = '003002001008'  # 성북구
GANGBUK = '003002001009'  # 강북구
DOBONG = '003002001010'  # 도봉구
NOWON = '003002001011'  # 노원구
EUNPYEONG = '003002001012'  # 은평구
SEODAEMUN = '003002001013'  # 서대문구
MAPO = '003002001014'  # 마포구
YANGCHEON = '003002001015'  # 양천구
GANGSEO = '003002001016'  # 강서구
GURO = '003002001017'  # 구로구
GEUMCHEON = '003002001018'  # 금천구
YEONGDEUNGPO = '003002001019'  # 영등포구
DONGJAK = '003002001020'  # 동작구
GWANAK = '003002001021'  # 관악구
SEOCHO = '003002001022'  # 서초구
GANGNAM = '003002001023'  # 강남구
SONGPA = '003002001024'  # 송파구
GANGDONG = '003002001025'  # 강동구

REGION = 'sc_plcyBizInstCd'  # 지역구분
BUSAN = '003002002'  # 부산
DAEGU = '003002003'  # 대구
INCHEON = '003002004'  # 인천
GWANGJU = '003002005'  # 광주
DAEJEON = '003002006'  # 대전
ULSAN = '003002007'  # 울산
GYEONGGI = '003002008'  # 경기
GANGWON = '003002009'  # 강원
NORTHCHUNGCHEONG = '003002010'  # 충북
SOUTHCHUNGCHEONG = '003002011'  # 충남
NORTHJEOLLA = '003002012'  # 전북
SOUTHJEOLLA = '003002013'  # 전남
NORTHGYEONGSANG = '003002014'  # 경북
SOUTHGYEONGSANG = '003002015'  # 경남
JEJU = '003002016'  # 제주
SEJONG = '003002017'  # 세종

CENTRAL = 'central'  # 중앙정부

# true/false

# 검색 ex1 서울시 (기본값)
print(requests.post("http://43.201.24.186:80/search",
                    json={
                        'pageIndex': 1,
                        'employmentTarget': 'employed',
                    },
                    headers={
                        'Content-type': 'application/json',
                        'Accept': 'application/json'
                    }
                    ).json())
# 검색 ex2 지역구분
print(requests.post("http://43.201.24.186:80/search",
                    json={
                        'pageIndex': 5,
                        'type': 'job',
                        'region': [
                            'busan',
                            'ulsan'
                        ]
                    },
                    headers={
                        'Content-type': 'application/json',
                        'Accept': 'application/json'
                    }
                    ).json())
# 검색 ex3 중앙정부
print(requests.post("http://43.201.24.186:80/search",
                    json={
                        'pageIndex': 5,
                        'recruitmentStatus': [
                            'regular',
                            'underWay',
                            'toBe',
                        ],
                        'central': ''
                    },
                    headers={
                        'Content-type': 'application/json',
                        'Accept': 'application/json'
                    }
                    ).json())
# 검색 ex4 서울시 자치구
print(requests.post("http://43.201.24.186:80/search",
                    json={
                        'pageIndex': 1,
                        'ageGroup': '19~24',
                        'agency': 'seoulGu',
                        'seoulRegion': [
                            'songpa',
                            'seongdong',
                        ],
                    },
                    headers={
                        'Content-type': 'application/json',
                        'Accept': 'application/json'
                    }
                    ).json())
# 검색 ex5 서울시 (명시)
response = requests.post("http://43.201.24.186:80/search",
                         json={
                            'pageIndex': 5,
                             'agency': 'seoulSi',
                         },
                         headers={
                             'Content-type': 'application/json',
                             'Accept': 'application/json'
                         }
                         ).json()
print(response)
# 검색 ex2 결과로 페이지 연결
# print(requests.get(f"http://43.201.24.186:80/page/{response[0]['pagekey']}", ).json())
